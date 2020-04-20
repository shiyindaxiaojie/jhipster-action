package org.ylzl.eden.uaa.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import org.ylzl.eden.spring.boot.data.jpa.repository.JpaRepository;
import org.ylzl.eden.uaa.domain.User;

import java.util.Date;
import java.util.List;

/**
 * 用户数据仓库
 *
 * @author gyl
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Date dateTime);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    User findOneByActivationKey(String activationKey);

    User findOneByEmailIgnoreCase(String email);

    User findOneByLogin(String login);

    User findOneByResetKey(String resetKey);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    User findOneWithAuthoritiesByEmail(String email);

    @EntityGraph(attributePaths = "authorities")
    User findOneWithAuthoritiesById(Long id);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    User findOneWithAuthoritiesByLogin(String login);
}
