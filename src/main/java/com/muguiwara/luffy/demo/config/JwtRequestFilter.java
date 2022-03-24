package com.muguiwara.luffy.demo.config;

import com.muguiwara.luffy.demo.service.UserDetailsServiceImpl;
import com.muguiwara.luffy.demo.token.JwtToken;
import com.muguiwara.luffy.demo.utils.SecurityConst;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String headerContent = request.getHeader(SecurityConst.AUTHORIZATION);
        if (headerContent.startsWith(SecurityConst.PREFIX)){
            String token = headerContent.substring(7);
            try{
                String username = jwtToken.getUsernameFromToken(token);
                if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    if (jwtToken.validatedToken(token, userDetails)){
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                                = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }catch (IllegalArgumentException e){
                logger.info("Unable to fetch JWT token"+e.getStackTrace());
            }catch (ExpiredJwtException e){
                logger.info("JWT token is expired"+e.getStackTrace());
            }catch (Exception e){
                logger.info(e.getMessage());
            }
        }else{
            logger.warn("JWT Token does not begin with Bearer String");
        }
        filterChain.doFilter(request,response);
    }
}
