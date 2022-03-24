package com.muguiwara.luffy.demo.repo;

import com.muguiwara.luffy.demo.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserEntityRepository extends MongoRepository<UserEntity, String> {

    UserEntity findByUsername(String username);
}
