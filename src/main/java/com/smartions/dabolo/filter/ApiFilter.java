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
		try {
			if(request.getParameter("userid").equals(RSAUtils.md5(request.getParameter("user_pub")))) {//验证公钥属于用户
				if(RSAUtils.verify(request.getParameter("timeout"), request.getParameter("user_pub"), request.getParameter("token"))) {//token验证
					long deference=Long.parseLong(request.getParameter("timeout"))-System.currentTimeMillis();
					if(deference<=0) {//授权过期
						
					}else {
						if(deference<10*60*1000) {//刷新过期时间
							
						}
						
						chain.doFilter(request, response);
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("Filter销毁中");
	}

}
