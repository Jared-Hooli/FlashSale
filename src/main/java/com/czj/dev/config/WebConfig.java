package com.czj.dev.config;

import java.util.List;

import com.czj.dev.access.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	private final UserArgumentResolver userArgumentResolver;
	private final AccessInterceptor accessInterceptor;

	public WebConfig(UserArgumentResolver userArgumentResolver, AccessInterceptor accessInterceptor) {
		this.userArgumentResolver = userArgumentResolver;
		this.accessInterceptor = accessInterceptor;
	}

	// 注册额外的参数解析器
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userArgumentResolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessInterceptor);
	}
}
