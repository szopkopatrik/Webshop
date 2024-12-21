package com.inn.webshop.com.inn.webshop.data.repository;

import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    UserEntity findByEmailAddress(@Param("email") String email);
}
