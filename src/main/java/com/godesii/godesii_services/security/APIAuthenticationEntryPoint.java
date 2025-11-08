//package com.godesii.godesii_services.security;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
//
//import java.io.IOException;
//
//public class APIAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
//    public APIAuthenticationEntryPoint(String loginFormUrl) {
//        super(loginFormUrl);
//    }
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        System.out.println(request);
//        if(request.getParameterMap().get("grant_type")[0].equals("password") || request.getRequestURI().startsWith("/api")){
//            return;
//        }
//        super.commence(request, response, authException);
//    }
//}
