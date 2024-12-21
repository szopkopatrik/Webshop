package com.inn.webshop.com.inn.webshop.data.repository;

import com.inn.webshop.com.inn.webshop.data.entity.RoleEntity;
import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    Optional<RoleEntity> findByRoleName(String roleName);
}
