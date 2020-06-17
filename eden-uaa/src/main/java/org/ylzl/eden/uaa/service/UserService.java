package org.ylzl.eden.uaa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ylzl.eden.spring.boot.support.service.JpaService;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.service.dto.UserDTO;

import java.util.Date;
import java.util.List;

/**
 * 用户业务接口
 *
 * @author gyl
 * @since 1.0.0
 */
public interface UserService extends JpaService<User, Long> {

  User createAndRegister(UserDTO userDTO, String password);

  User create(UserDTO userDTO);

  void deleteNotActivatedUsers();

  void delete(String login);

  List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Date dateTime);

  Page<User> findAllManagedUsers(Pageable pageable);

  User findOneByActivationKey(String activationKey);

  User findOneByEmailIgnoreCase(String email);

  User findOneByLogin(String login);

  User findOneByResetKey(String resetKey);

  User findOneWithAuthorities();

  User findOneWithAuthorities(Long id);

  User findOneWithAuthoritiesByLogin(String login);

  User updateActivation(String key);

  void updatePassword(String currentPassword, String newPassword);

  User updatePasswordByKey(String newPassword, String key);

  User updatePasswordByEmail(String mail);

  User update(UserDTO userDTO);

  String generatePasswordResetLink(String resetKey);
}
