package org.ylzl.eden.uaa.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylzl.eden.spring.boot.commons.lang.StringConstants;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.mail.EnhancedMailProperties;
import org.ylzl.eden.spring.boot.framework.web.rest.errors.EntityNotFoundException;
import org.ylzl.eden.spring.boot.framework.web.rest.errors.InvalidPrimaryKeyException;
import org.ylzl.eden.spring.boot.security.core.SecurityConstants;
import org.ylzl.eden.spring.boot.security.core.util.SpringSecurityUtils;
import org.ylzl.eden.spring.boot.security.web.rest.error.InvalidPasswordException;
import org.ylzl.eden.spring.boot.security.web.rest.error.UserNotActivatedException;
import org.ylzl.eden.spring.boot.support.service.impl.JpaServiceImpl;
import org.ylzl.eden.uaa.config.ApplicationProperties;
import org.ylzl.eden.uaa.domain.Authority;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.repository.UserRepository;
import org.ylzl.eden.uaa.service.AuthorityService;
import org.ylzl.eden.uaa.service.UserService;
import org.ylzl.eden.uaa.service.dto.UserDTO;
import org.ylzl.eden.uaa.service.mapstruct.UserMapstruct;
import org.ylzl.eden.uaa.service.util.GenerateUtils;
import org.ylzl.eden.uaa.web.rest.errors.EmailAlreadyUsedException;
import org.ylzl.eden.uaa.web.rest.errors.LoginAlreadyUsedException;

import java.util.*;

/**
 * 用户业务实现
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Service("userService")
public class UserServiceImpl extends JpaServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CacheManager cacheManager;

    private final AuthorityService authorityService;

    private final EnhancedMailProperties enhancedMailProperties;

    private final ApplicationProperties applicationProperties;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
						   CacheManager cacheManager, AuthorityService authorityService,
						   EnhancedMailProperties enhancedMailProperties, ApplicationProperties applicationProperties) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cacheManager = cacheManager;
        this.authorityService = authorityService;
        this.enhancedMailProperties = enhancedMailProperties;
        this.applicationProperties = applicationProperties;
    }

    @Transactional
    @Override
    public User createAndRegister(UserDTO userDTO, String password) {
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin());
        newUser.setPassword(encryptedPassword);
        newUser.setEmail(userDTO.getEmail());
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setActivated(false);
        newUser.setActivationKey(GenerateUtils.generateActivationKey());
        // 默认添加用户权限
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityService.findOneByCode(SecurityConstants.ROLE_USER);
        if (authority == null) {
            log.warn("数据库无法找到用户权限：{}", SecurityConstants.ROLE_USER);
            throw new EntityNotFoundException();
        }
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("注册用户成功：{}", newUser);
        return newUser;
    }

    @Transactional
    @Override
    public User create(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            throw new InvalidPrimaryKeyException();
        }

        if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()) != null) {
            throw new LoginAlreadyUsedException();
        }

        if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()) != null) {
            throw new EmailAlreadyUsedException();
        }

        User user = UserMapstruct.INSTANCE.userDTOToUser(userDTO);
        if (userDTO.getLangKey() == null) {
            user.setLangKey(FrameworkConstants.DEFAULT_LANGUAGE);
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(GenerateUtils.generateResetKey());
        user.setResetDate(new Date());
        user.setActivated(true);
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("创建用户成功：{}", user);
        return user;
    }

	@Transactional
    @Override
    public void deleteNotActivatedUsers() {
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(
            DateUtils.addDays(new Date(), applicationProperties.getUser().getRemoveNotActivatedAmount()));
        if (users == null || users.isEmpty()) {
            return;
        }
        StringBuilder msg = new StringBuilder();
        msg.append("删除未激活用户：");
        int i = 0;
        for (User user : users) {
            msg.append(user.getLogin());
            if (i < users.size() - 1) {
                msg.append(StringConstants.COMMA);
            }
            userRepository.delete(user);
            this.clearUserCaches(user);
            i++;
        }
        log.info(msg.toString());
    }

    @Transactional
    @Override
    public void delete(String login) {
        User user = userRepository.findOneByLogin(login);
        if (user == null) {
            log.warn("删除失败，用户无效：{}", login);
            throw new EntityNotFoundException();
        }
        userRepository.delete(user);
        this.clearUserCaches(user);
        log.debug("删除用户成功：{}", user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Date dateTime) {
        return userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(dateTime);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<User> findAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, FrameworkConstants.ANONYMOUS_USER);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneByActivationKey(String activationKey) {
        return userRepository.findOneByActivationKey(activationKey);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneByEmailIgnoreCase(String email) {
        return userRepository.findOneByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneByResetKey(String resetKey) {
        return userRepository.findOneByResetKey(resetKey);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneWithAuthorities() {
        String login = SpringSecurityUtils.getCurrentUserLogin();
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneWithAuthorities(Long id) {
        return userRepository.findOneWithAuthoritiesById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public User findOneWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional
    @Override
    public User updateActivation(String key) {
        log.debug("激活密钥：{}", key);
        User user = userRepository.findOneByActivationKey(key);
        if (user == null) {
            log.warn("激活用户的密钥无效：{}", key);
            throw new EntityNotFoundException();
        }
        user.setActivated(true);
        user.setActivationKey(null);
        this.clearUserCaches(user);
        log.debug("激活用户成功：{}", user);
        return user;
    }

    @Transactional
    @Override
    public void updatePassword(String currentPassword, String newPassword) {
        String login = SpringSecurityUtils.getCurrentUserLogin();
        User user = userRepository.findOneByLogin(login);
        if (user == null) {
            log.warn("数据库无法找到当前登录用户：{}", login);
            throw new EntityNotFoundException();
        }
        String currentEncryptedPassword = user.getPassword();
        if (!passwordEncoder.matches(currentPassword, currentEncryptedPassword)) {
            throw new InvalidPasswordException();
        }
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        this.clearUserCaches(user);
        log.debug("用户修改密码成功：{}", user);
    }

    @Transactional
    @Override
    public User updatePasswordByKey(String newPassword, String key) {
        return null;
    }

    @Transactional
    @Override
    public User updatePasswordByEmail(String mail) {
        User user = userRepository.findOneByEmailIgnoreCase(mail);
        if (user == null) {
            log.warn("用户邮箱无效：{}", mail);
            throw new EntityNotFoundException();
        }
        if (!user.getActivated()) {
            log.warn("用户未激活：{}", user.getLogin());
            throw new UserNotActivatedException();
        }
        user.setResetKey(GenerateUtils.generateResetKey());
        user.setResetDate(new Date());
        this.clearUserCaches(user);
        return user;
    }

    @Transactional
    @Override
    public User update(UserDTO userDTO) {
        User existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser != null && (!existingUser.getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }

        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser != null && (!existingUser.getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }

        User user = userRepository.findOne(userDTO.getId());
        if (user == null) {
            log.warn("用户主键无效：{}", userDTO.getId());
            throw new EntityNotFoundException();
        }
        this.clearUserCaches(user);
        user = UserMapstruct.INSTANCE.userDTOToUser(userDTO);
        Set<Authority> managedAuthorities = user.getAuthorities();
        managedAuthorities.clear();
        this.updateAuthorities(userDTO.getAuthorities(), managedAuthorities);
        log.debug("修改用户成功：{}", user);
        return user;
    }

    @Override
    public String generatePasswordResetLink(String resetKey) {
        String baseUrl = enhancedMailProperties.getBaseUrl();
        return baseUrl + "/#/password-reset/finish?key=" + resetKey;
    }

    private void updateAuthorities(Set<Authority> authoritieDTOs, Set<Authority> managedAuthorities) {
        Authority authority;
        for (Authority authorityDTO : authoritieDTOs) {
            authority = authorityService.findOne(authorityDTO.getId());
            if (authority == null) {
                log.warn("存在无效的权限主键：{}", authorityDTO.getId());
                throw new EntityNotFoundException();
            }
            managedAuthorities.add(authority);
        }
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
    }
}
