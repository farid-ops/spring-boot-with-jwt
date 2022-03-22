package com.muguiwara.luffy.demo.repo;

import com.muguiwara.luffy.demo.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends MongoRepository<UserEntity, String> {

    UserEntity findByUsername(String username);
}
