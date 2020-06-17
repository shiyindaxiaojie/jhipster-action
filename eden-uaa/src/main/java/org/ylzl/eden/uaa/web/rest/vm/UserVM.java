package org.ylzl.eden.uaa.web.rest.vm;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.ylzl.eden.uaa.domain.User;

/**
 * 用户视图模型
 *
 * @author gyl
 * @since 1.0.0
 */
@ApiModel(description = "用户视图模型")
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserVM extends User {}
