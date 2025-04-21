package pe.edu.upeu.sysalmacen.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORS implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // The init method is empty because no initialization is required for this filter.
        // If initialization is needed (e.g., reading configurations from filterConfig), 
        // it can be implemented here.
        // Otherwise, we can leave it as-is or throw an UnsupportedOperationException 
        // if we want to make it explicit that it’s not used.
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        // Allowing CORS requests from any origin
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // Specifying allowed HTTP methods for CORS
        response.setHeader("Access-Control-Allow-Methods", "DELETE, GET, OPTIONS, PATCH, POST, PUT");
        
        // Setting CORS pre-flight response cache duration (in seconds)
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Allowing specific headers in CORS requests
        response.setHeader("Access-Control-Allow-Headers", 
                "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
        
        // If it's a pre-flight OPTIONS request, return a status of OK
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Otherwise, continue with the request chain
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
        // The destroy method is empty because there is no resource to clean up.
        // If needed, cleanup logic (e.g., releasing resources) can be added here.
        // Alternatively, throw UnsupportedOperationException if cleanup is not required.
    }
}
