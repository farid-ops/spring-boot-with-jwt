package com.muguiwara.luffy.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@Getter
@Setter
public class RoleEntity {
    @Id
    private String id;
    private String rolename;

    public RoleEntity(){
        super();
    }
}
