package com.inn.webshop.com.inn.webshop.dao;

import com.inn.webshop.com.inn.webshop.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);

}
