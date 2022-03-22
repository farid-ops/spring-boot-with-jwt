package com.muguiwara.luffy.demo.service;

import com.muguiwara.luffy.demo.model.UserEntity;
import com.muguiwara.luffy.demo.repo.UserEntityRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    private UserEntityRepository userEntityRepository;

    public JWTUserDetailsService(UserEntityRepository userEntityRepository){
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = this.userEntityRepository.findUserByUsername(username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER_ROLE"));
        return new User(userEntity.getUsername(), userEntity.getPassword(), authorities);
    }
}
