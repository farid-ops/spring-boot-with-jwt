package com.muguiwara.luffy.demo.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long JWT_VALIDITY_TOKEN = 50 * 60 *60;
    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public Claims getAllClaimsFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public <T> T getClaimFromToken(String token, Function<Claims , T> claimsFunction){
        final Claims claims = this.getAllClaimsFromToken(token);
        return claimsFunction.apply(claims);
    }

    public String getUsernameFromToken(String token){
        return this.getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token){
        return this.getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean isExpired(String token){
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, String> claims = new HashMap<>();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() * JWT_VALIDITY_TOKEN * 1000)).signWith(key).compact();
    }

    public boolean validatedToken(String token, UserDetails userDetails){
        String username = this.getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !this.isExpired(token));
    }
}
