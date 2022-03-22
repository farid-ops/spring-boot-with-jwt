package com.muguiwara.luffy.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Getter
@Setter
public class UserEntity {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    @Indexed(unique = true)
    private String email;
    private String role;

    public UserEntity(){
        super();
    }
}
