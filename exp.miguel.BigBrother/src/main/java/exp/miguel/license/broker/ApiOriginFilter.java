package exp.miguel.license.broker;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T04:00:38.477Z")

// I added this file in a vain attempt to make it work. It didn't. So I commented out the @Component bean to disable it.
//@Component
public class ApiOriginFilter implements javax.servlet.Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
	                     FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		res.addHeader("Access-Control-Allow-Headers", "Content-Type");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
