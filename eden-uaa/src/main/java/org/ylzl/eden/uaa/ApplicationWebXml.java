package org.ylzl.eden.uaa;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.ylzl.eden.spring.boot.framework.core.util.SpringProfileUtils;

/**
 * Spring Boot Servlet 加载器
 *
 * @author gyl
 * @since 1.0.0
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    SpringProfileUtils.addDefaultProfile(builder.application());
    return builder.sources(Application.class);
  }
}
