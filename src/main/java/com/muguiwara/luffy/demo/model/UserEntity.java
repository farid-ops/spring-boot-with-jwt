package com.muguiwara.luffy.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;

@Document("users")
@AllArgsConstructor
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
    @DBRef
    private Collection<RoleEntity> roleEntities = new ArrayList<>();

    public UserEntity(){
        super();
    }
}
