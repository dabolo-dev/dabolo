package com.smartions.dabolo.filter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

import com.smartions.dabolo.model.User;
import com.smartions.dabolo.utils.RSAUtils;
@Component
@ServletComponentScan
@WebFilter(urlPatterns = "/*",filterName = "apiFilter")
public class ApiFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("Filter初始化中");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		System.out.println(123456);
		chain.doFilter(request, response);
		
		
		

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("Filter销毁中");
	}

}
