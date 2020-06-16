package org.ylzl.eden.uaa.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ylzl.eden.spring.boot.security.web.rest.error.UserNotActivatedException;
import org.ylzl.eden.uaa.domain.Authority;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.service.UserService;

import java.util.List;

/**
 * 用户认证详情服务
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

  private final UserService userService;

  public DomainUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Check username {} is valid", username);

    if (new EmailValidator().isValid(username, null)) {
      User user = userService.findOneWithAuthoritiesByEmail(username);
      return this.createSpringSecurityUser(username, user);
    }

    String lowercaseLogin = username.toLowerCase();
    User user = userService.findOneWithAuthoritiesByLogin(lowercaseLogin);
    return this.createSpringSecurityUser(lowercaseLogin, user);
  }

  private org.springframework.security.core.userdetails.User createSpringSecurityUser(
      String lowercaseLogin, User user) {
    if (!user.getActivated()) {
      throw new UserNotActivatedException(
          String.format("Login name %s is not activated", lowercaseLogin));
    }

    List<SimpleGrantedAuthority> simpleGrantedAuthorities = Lists.newArrayList();
    for (Authority authority : user.getAuthorities()) {
      simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority.getCode()));
    }
    return new org.springframework.security.core.userdetails.User(
        user.getLogin(), user.getPassword(), simpleGrantedAuthorities);
  }
}
