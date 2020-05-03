package org.ylzl.eden.uaa.repository;

import org.springframework.stereotype.Repository;
import org.ylzl.eden.spring.boot.data.jpa.repository.JpaRepository;
import org.ylzl.eden.uaa.domain.Authority;

/**
 * 权限数据仓库
 *
 * @author gyl
 * @since 0.0.1
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

  Authority findOneByCode(String code);
}
