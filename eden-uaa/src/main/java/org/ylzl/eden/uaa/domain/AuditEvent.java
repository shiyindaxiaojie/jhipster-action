package org.ylzl.eden.uaa.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.ylzl.eden.spring.boot.data.audit.event.PersistentAuditEvent;
import org.ylzl.eden.spring.boot.data.jpa.id.JpaIdentifierGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计事件域对象
 *
 * @author gyl
 * @since 0.0.1
 */
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@GenericGenerator(name = JpaIdentifierGenerator.NAME, strategy = JpaIdentifierGenerator.STRATEGY)
@Table(name = "uaa_audit_event")
public class AuditEvent implements PersistentAuditEvent {

  private static final long serialVersionUID = 8971840794784917766L;

  @Getter
  @Setter
  @Id
  @GeneratedValue(generator = JpaIdentifierGenerator.NAME)
  private Long id;

  @NotNull
  @Column(nullable = false)
  private String principal;

  @Column(name = "event_date")
  private Date eventDate;

  @Column(name = "event_type")
  private String eventType;

  @ElementCollection
  @MapKeyColumn(name = "name")
  @Column(name = "value")
  @CollectionTable(name = "uaa_audit_event_data", joinColumns = @JoinColumn(name = "id"))
  private Map<String, String> data = new HashMap<>();

  @Override
  public String getPrincipal() {
    return principal;
  }

  @Override
  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  @Override
  public Date getEventDate() {
    return eventDate;
  }

  @Override
  public void setEventDate(Date eventDate) {
    this.eventDate = eventDate;
  }

  @Override
  public String getEventType() {
    return eventType;
  }

  @Override
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  @Override
  public Map<String, String> getData() {
    return data;
  }

  @Override
  public void setData(Map<String, String> data) {
    this.data = data;
  }
}
