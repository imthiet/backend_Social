package com.example.manageruser.WskConfig;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;

        if (exception.getMessage().equals("User not found with username")) {
            errorMessage = "Username does not exist!";
        } else if (exception.getMessage().equals("Account not enabled. Please verify your email.")) {
            errorMessage = "Your account is not enabled. Please verify your email.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid error get!!";
        } else {
            errorMessage = "Login failed. Please check your info or verify your email and try again.";
        }

        response.sendRedirect("/login?error=" + errorMessage);
    }
}