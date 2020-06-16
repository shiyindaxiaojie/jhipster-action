package org.ylzl.eden.uaa.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.web.rest.errors.EntityNotFoundException;
import org.ylzl.eden.spring.boot.framework.web.rest.errors.InvalidPrimaryKeyException;
import org.ylzl.eden.spring.boot.support.service.impl.JpaServiceImpl;
import org.ylzl.eden.uaa.domain.Authority;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.repository.UserRepository;
import org.ylzl.eden.uaa.service.AuthorityService;
import org.ylzl.eden.uaa.service.UserService;
import org.ylzl.eden.uaa.service.dto.UserDTO;
import org.ylzl.eden.uaa.service.mapper.UserMapper;
import org.ylzl.eden.uaa.service.util.GenerateUtils;
import org.ylzl.eden.uaa.web.rest.errors.EmailAlreadyUsedException;
import org.ylzl.eden.uaa.web.rest.errors.LoginAlreadyUsedException;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

  public UserServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      CacheManager cacheManager,
      AuthorityService authorityService) {
    super(userRepository);
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.cacheManager = cacheManager;
    this.authorityService = authorityService;
  }

  @Transactional
  @Override
  public User create(UserDTO userDTO) {
    if (userDTO.getId() != null) {
      throw new InvalidPrimaryKeyException();
    }

    userRepository
        .findOneByLogin(userDTO.getLogin().toLowerCase())
        .ifPresent(
            user -> {
              throw new LoginAlreadyUsedException(userDTO.getLogin());
            });

    userRepository
        .findOneByEmailIgnoreCase(userDTO.getEmail())
        .ifPresent(
            user -> {
              throw new EmailAlreadyUsedException(userDTO.getEmail());
            });

    User user = UserMapper.INSTANCE.userDTOToUser(userDTO);
    user.setLogin(userDTO.getLogin().toLowerCase());

    if (userDTO.getLangKey() == null) {
      user.setLangKey(FrameworkConstants.DEFAULT_LANGUAGE);
    } else {
      user.setLangKey(userDTO.getLangKey());
    }

    String encryptedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encryptedPassword);
    user.setResetKey(GenerateUtils.generateResetKey());
    user.setResetDate(Instant.now());
    user.setActivated(true);

    userRepository.save(user);
    this.clearUserCaches(user);

    log.debug("Created success for User: {}", user);
    return user;
  }

  @Transactional
  @Override
  public User update(UserDTO userDTO) {

    userDTO.setLogin(userDTO.getLogin().toLowerCase());
    userRepository
        .findOneByLogin(userDTO.getLogin())
        .ifPresent(
            user -> {
              if (!user.getId().equals(userDTO.getId())) {
                throw new LoginAlreadyUsedException(userDTO.getLogin());
              }
            });

    userRepository
        .findOneByEmailIgnoreCase(userDTO.getEmail())
        .ifPresent(
            user -> {
              if (!user.getId().equals(userDTO.getId())) {
                throw new EmailAlreadyUsedException(userDTO.getEmail());
              }
            });

    User user =
        userRepository.findById(userDTO.getId()).orElseThrow(InvalidPrimaryKeyException::new);

    this.clearUserCaches(user);
    UserMapper.INSTANCE.updateUserFromUserDTO(userDTO, user);

    Set<Authority> managedAuthorities = user.getAuthorities();
    managedAuthorities.clear();
    userDTO.getAuthorities().stream()
        .map(Authority::getId)
        .map(authorityService::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(managedAuthorities::add);

    log.debug("Updated success for User: {}", user);
    return user;
  }

  @Transactional
  @Override
  public void delete(String login) {

    userRepository
        .findOneByLogin(login.toLowerCase())
        .ifPresent(
            user -> {
              userRepository.delete(user);
              this.clearUserCaches(user);
              log.debug("Deleted success for User: {}", user);
            });
  }

  @Override
  public Page<User> findAllManagedUsers(Pageable pageable) {
    return userRepository.findAllByLoginNot(pageable, FrameworkConstants.ANONYMOUS_USER);
  }

  @Override
  public User findOneWithAuthoritiesByLogin(String login) {
    return userRepository
        .findOneWithAuthoritiesByLogin(login.toLowerCase())
        .orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public User findOneWithAuthoritiesByEmail(String email) {
    return userRepository
        .findOneWithAuthoritiesByEmail(email)
        .orElseThrow(EntityNotFoundException::new);
  }

  private void clearUserCaches(User user) {
    Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE))
        .evict(user.getEmail());
    Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE))
        .evict(user.getLogin());
  }
}
