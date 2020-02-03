package org.ylzl.eden.uaa.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylzl.eden.spring.boot.support.service.dto.PageableDTO;

import java.util.Date;

/**
 * 用户分页数据传输对象
 *
 * @author gyl
 * @since 0.0.1
 */
@ApiModel(description = "用户分页数据传输对象")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPageableDTO extends PageableDTO {

    @ApiModelProperty(value = "账号")
    private String login;

    @ApiModelProperty(value = "创建时间（开始）")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDateStart;

    @ApiModelProperty(value = "创建时间（结束）")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDateEnd;

    @ApiModelProperty(value = "最后修改时间（开始）")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDateStart;

    @ApiModelProperty(value = "最后修改时间（结束）")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastModifiedDateEnd;
}
