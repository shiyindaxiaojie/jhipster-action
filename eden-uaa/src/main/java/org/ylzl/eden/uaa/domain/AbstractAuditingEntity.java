package org.ylzl.eden.uaa.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 抽象审计实体
 *
 * @author gyl
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class AbstractAuditingEntity implements Serializable {

  private static final long serialVersionUID = -2652963832628969831L;

  @ApiModelProperty(value = "创建帐号")
  @CreatedBy
  @Column(name = "created_by", nullable = false, length = 20, updatable = false)
  private String createdBy;

  @ApiModelProperty(value = "创建时间")
  @CreatedDate
  @Column(name = "created_date", nullable = false)
  private Date createdDate;

  @ApiModelProperty(value = "最后修改帐号")
  @LastModifiedBy
  @Column(name = "last_modified_by", length = 20)
  private String lastModifiedBy;

  @ApiModelProperty(value = "最后修改时间")
  @LastModifiedDate
  @Column(name = "last_modified_date")
  private Date lastModifiedDate;
}
