package org.ylzl.eden.uaa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.ylzl.eden.spring.boot.support.SpringBootApplicationAdapter;

/**
 * Spring Boot 引导类
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@SpringBootApplication
public class Application extends SpringBootApplicationAdapter {

	public Application(Environment env) {
		super(env);
	}

	/**
	 * 启动入口
	 *
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		Environment env = run(app, args);
		logApplicationServerAfterRunning(env);
		logConfigServerAfterRunning(env);
	}
}
