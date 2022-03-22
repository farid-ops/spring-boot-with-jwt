package com.muguiwara.luffy.demo.config;

import com.muguiwara.luffy.demo.service.JWTUserDetailsService;
import com.muguiwara.luffy.demo.token.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final JWTUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(JWTUserDetailsService jwtUserDetailsService,
                            JwtTokenUtil jwtTokenUtil){
        this.userDetailsService = jwtUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        if (StringUtils.startsWith(requestTokenHeader, "Bearer ")){
            String token = requestTokenHeader.substring(7);
            try{
                String username = this.jwtTokenUtil.getUsernameFromToken(token);
                if (StringUtils.isNotEmpty(username) && null == SecurityContextHolder.getContext().getAuthentication()) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (this.jwtTokenUtil.validatedToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                                        null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.error("Unable to fetch JWT Token");
            } catch (ExpiredJwtException e) {
                logger.error("JWT Token is expired");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
        filterChain.doFilter(request, response);
    }
}
