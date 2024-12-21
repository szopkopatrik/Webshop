package com.inn.webshop.com.inn.webshop.data.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@NamedQuery(name = "Role.findByRoleId", query = "select u from RoleEntity u where u.roleId=:role_id")

@Data
@Entity
@Table(name = "roles")
@DynamicInsert
@DynamicUpdate
public class RoleEntity {


    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id")
    private int roleId;

    @Column(name="role_name")
    private String roleName;

    public RoleEntity() {
    }

    public RoleEntity(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

}
