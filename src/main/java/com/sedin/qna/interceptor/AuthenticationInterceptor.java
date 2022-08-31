package com.sedin.qna.interceptor;

import com.sedin.qna.athentication.service.AuthenticationService;
import com.sedin.qna.exception.InvalidTokenException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final String ACCOUNT_ID = "accountId";
    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer ";

    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getRequestURI().startsWith("/api/accounts") && request.getMethod().equals(HttpMethod.POST.toString())) {
            return true;
        }

        String authorization = request.getHeader(AUTHORIZATION);

        if (!authorization.startsWith(BEARER)) {
            throw new InvalidTokenException();
        }

        String accessToken = authorization.substring(BEARER.length());
        request.setAttribute(ACCOUNT_ID, authenticationService.decodeAccessToken(accessToken));

        return true;
    }
}
