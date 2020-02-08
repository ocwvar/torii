package com.ocwvar.torii;

import com.ocwvar.utils.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局的拦截处理器
 */
@Component
public class ToriiInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
		Log.getInstance().print( "收到 " + request.getMethod() + " 请求：" + request.getRequestURL() );
		return true;
	}

}
