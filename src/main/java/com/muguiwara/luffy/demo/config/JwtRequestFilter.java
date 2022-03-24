package com.muguiwara.luffy.demo.config;

import com.muguiwara.luffy.demo.token.JwtToken;
import com.muguiwara.luffy.demo.utils.SecurityConst;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtToken jwtToken;

    public JwtRequestFilter(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                            JwtToken jwtToken) {
        this.userDetailsService = userDetailsService;
        this.jwtToken = jwtToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

       final String authorizationHeader = request.getHeader(SecurityConst.AUTHORIZATION);
       String username = null, jwt = null;

       if (null != authorizationHeader && authorizationHeader.startsWith(SecurityConst.PREFIX)){
           jwt = authorizationHeader.substring(7);
           username = this.jwtToken.getUsernameFromToken(jwt);
       }
       if (null != username && null == SecurityContextHolder.getContext().getAuthentication()){
           UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
           UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken= new
                   UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
           usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
           SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
       }
       filterChain.doFilter(request, response);
    }
}
