package com.muguiwara.luffy.demo.api;

import com.muguiwara.luffy.demo.model.UserEntity;
import com.muguiwara.luffy.demo.repo.UserEntityRepository;
import com.muguiwara.luffy.demo.token.JwtToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/auth")
public class AuthRestController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserEntityRepository userEntityRepository;
    private final UserDetailsService userDetailsService;
    private final JwtToken jwtToken;
    private final AuthenticationManager authenticationManager;

    public AuthRestController(UserEntityRepository userEntityRepository,
                              @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                              JwtToken jwtToken,
                              AuthenticationManager authenticationManager) {
        this.userEntityRepository = userEntityRepository;
        this.userDetailsService = userDetailsService;
        this.jwtToken = jwtToken;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserEntity user){
        Map<String, Object> response = new HashMap<>();
        try{
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            if (authentication.isAuthenticated()){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getUsername());
                String token = this.jwtToken.generateToken(userDetails);
                response.put("Error", false);
                response.put("Loggin", true);
                response.put("token", token);
                return ResponseEntity.status(200).body(response);
            }else{
                response.put("Error", true);
                response.put("Message", "Invalid credential");
                return ResponseEntity.status(401).body(response);
            }
        } catch (DisabledException e) {
            e.printStackTrace();
            response.put("error", true);
            response.put("message", "User is disabled");
            return ResponseEntity.status(500).body(response);
        } catch (BadCredentialsException e) {
            response.put("error", true);
            response.put("message", "Invalid Credentials");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", true);
            response.put("message", "Something went wrong");
            return ResponseEntity.status(500).body(response);
        }
    }
}
