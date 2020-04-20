package org.ylzl.eden.uaa.service.dto;

import org.ylzl.eden.uaa.domain.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户数据传输对象
 *
 * @author gyl
 * @since 1.0.0
 */
@ApiModel(description = "用户数据传输对象")
@AllArgsConstructor
@Data
public class UserDTO extends User {

}
