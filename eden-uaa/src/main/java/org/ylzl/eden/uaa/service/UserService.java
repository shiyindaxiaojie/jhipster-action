package org.ylzl.eden.uaa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.ylzl.eden.spring.boot.support.service.JpaService;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.service.dto.UserDTO;

/**
 * 用户业务接口
 *
 * @author gyl
 * @since 1.0.0
 */
public interface UserService extends JpaService<User, Long> {

  User create(UserDTO userDTO);

  void delete(String login);

  Page<User> findAllManagedUsers(Pageable pageable);

  User findOneWithAuthoritiesByLogin(String login);

  User findOneWithAuthoritiesByEmail(String email);

  User update(UserDTO userDTO);
}
