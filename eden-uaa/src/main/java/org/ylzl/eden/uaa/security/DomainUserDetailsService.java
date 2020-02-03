package org.ylzl.eden.uaa.security;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ylzl.eden.spring.boot.framework.web.rest.errors.EntityNotFoundException;
import org.ylzl.eden.spring.boot.security.web.rest.error.UserNotActivatedException;
import org.ylzl.eden.uaa.domain.Authority;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户认证详情服务
 *
 * @author gyl
 * @since 0.0.1
 */
@Slf4j
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
    	this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("验证用户是否有效：{}", username);

        if (new EmailValidator().isValid(username, null)) {
            User user = userRepository.findOneWithAuthoritiesByEmail(username);
            if (user == null) {
                throw new EntityNotFoundException(String.format("用户邮箱不存在：%s", username));
            }
            return this.createSpringSecurityUser(username, user);
        }

        String lowercaseLogin = username.toLowerCase();
        User user = userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin);
        return this.createSpringSecurityUser(lowercaseLogin, user);
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin,
                                                                                        User user) {
        if (!user.getActivated()) {
            throw new UserNotActivatedException(String.format("用户帐号未激活：%s", lowercaseLogin));
        }

        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        for (Authority authority : user.getAuthorities()) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority.getCode()));
        }
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), simpleGrantedAuthorities);
    }
}
