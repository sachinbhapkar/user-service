package com.user.security;


import com.user.service.impl.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    UserDetailsImpl userDetails;

    @Value("secret.key")
    String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String parsedJwtToken = parseJwt(request);
            if(parsedJwtToken != null && validateToken(parsedJwtToken)){
                String usernameFromToken = getUsernameFromToken(parsedJwtToken);
                UserDetails loadedUserName = userDetails.loadUserByUsername(usernameFromToken);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loadedUserName,null);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }

    private String getUsernameFromToken(String parsedJwtToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(parsedJwtToken).getBody().getSubject();
    }

    private boolean validateToken(String parsedJwtToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(parsedJwtToken);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuthToken = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuthToken) && headerAuthToken.startsWith("Bearer")){
            return headerAuthToken.substring(7);
        }
        return null;
    }
}
