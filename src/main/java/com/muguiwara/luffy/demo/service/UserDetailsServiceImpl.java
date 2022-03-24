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

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private final UserEntityRepository userEntityRepository;

    public UserDetailsServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userEntityRepository.findByUsername(username);
        if (null == user)
            throw new UsernameNotFoundException("User doesn't exist with username "+username);
        Collection<GrantedAuthority> authorities = user.getRoleEntities().stream()
                .map(authority-> new SimpleGrantedAuthority(authority.getRolename())).collect(Collectors.toList());
        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        return this.userEntityRepository.save(userEntity);
    }
}
