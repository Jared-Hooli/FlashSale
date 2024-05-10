package com.czj.dev.config;

import com.czj.dev.access.UserContext;
import com.czj.dev.domain.User;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
	// 该方法的返回true则表明要解析该参数
	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		// 获取要解析的参数类型
		Class<?> clazz = methodParameter.getParameterType();
		// 只有当该返回值为true时，才会调用下面的resolveArgument方法解析参数
		return clazz == User.class;
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
			NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
		// 将UserContext的getUser()方法的返回值作为User参数的值
		return UserContext.getUser();
	}
}
