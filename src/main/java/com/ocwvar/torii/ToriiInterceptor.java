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

	/*

		request.getRequestURL()		:	http://127.0.0.1/core/KFC:J:A:A:2019100800/services/get
		request.getRequestURI()		:	/core/KFC:J:A:A:2019100800/services/get
		request.getServerName()		:	127.0.0.1

	 */
	@Override
	public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
		Log.getInstance().print( "收到 " + request.getMethod() + " 请求：" + request.getRequestURL() );
		return true;
	}

}
