package org.ylzl.eden.uaa.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.ylzl.eden.spring.boot.data.jpa.id.JpaIdentifierGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 权限域对象
 *
 * @author gyl
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@GenericGenerator(name = JpaIdentifierGenerator.NAME, strategy = JpaIdentifierGenerator.STRATEGY)
@Table(name = "uaa_authority")
public class Authority implements Serializable {

  private static final long serialVersionUID = -7063643228110486345L;

  @ApiModelProperty(value = "主键")
  @Id
  private Long id;

  @ApiModelProperty(value = "名称")
  @NotNull
  @Size(max = 20)
  @Column(length = 20)
  private String name;

  @ApiModelProperty(value = "代码")
  @NotNull
  @Size(max = 20)
  @Column(length = 20)
  private String code;
}
