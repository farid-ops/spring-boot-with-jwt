package com.muguiwara.luffy.demo.repo;

import com.muguiwara.luffy.demo.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends MongoRepository<UserEntity, String> {

    @Query(value = "{username: '?0'}")
    UserEntity findUserByUsername(String username);
}
