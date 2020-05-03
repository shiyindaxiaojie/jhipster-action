package org.ylzl.eden.uaa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.ylzl.eden.spring.boot.data.jpa.id.JpaIdentifierGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户领域
 *
 * @author gyl
 * @since 0.0.1
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@GenericGenerator(name = JpaIdentifierGenerator.NAME, strategy = JpaIdentifierGenerator.STRATEGY)
@Table(name = "uaa_user")
public class User extends AbstractAuditingEntity {

  private static final long serialVersionUID = 5816295498204890068L;

  @ApiModelProperty(value = "主键")
  @Id
  @GeneratedValue(generator = JpaIdentifierGenerator.NAME)
  private Long id;

  @ApiModelProperty(value = "账号", required = true)
  @NotBlank
  @Size(min = 1, max = 20)
  @Column(name = "login", length = 20, unique = true, nullable = false)
  private String login;

  @ApiModelProperty(value = "密码", required = true)
  @NotBlank
  @Size(max = 60)
  @Column(name = "password_hash", length = 60, nullable = false)
  private String password;

  @ApiModelProperty(value = "邮箱")
  @Email
  @Size(min = 5, max = 254)
  @Column(length = 254, unique = true)
  private String email;

  @ApiModelProperty(hidden = true)
  @Column(nullable = false)
  private Boolean activated = false;

  @ApiModelProperty(hidden = true)
  @Column(nullable = false)
  private Boolean locked = false;

  @ApiModelProperty(value = "语言")
  @Size(min = 2, max = 6)
  @Column(name = "lang_key", length = 6)
  private String langKey;

  @ApiModelProperty(hidden = true)
  @Size(max = 20)
  @Column(name = "activation_key", length = 20)
  @JsonIgnore
  private String activationKey;

  @ApiModelProperty(hidden = true)
  @Size(max = 20)
  @Column(name = "reset_key", length = 20)
  @JsonIgnore
  private String resetKey;

  @ApiModelProperty(hidden = true)
  @Column(name = "reset_date")
  @JsonIgnore
  private Date resetDate;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "uaa_user_authority",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
  @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @BatchSize(size = 20)
  private Set<Authority> authorities = new HashSet<Authority>();
}
