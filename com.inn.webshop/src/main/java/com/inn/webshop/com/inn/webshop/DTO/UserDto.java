package com.inn.webshop.com.inn.webshop.DTO;

import com.inn.webshop.com.inn.webshop.data.entity.RoleEntity;
import lombok.Data;

import java.util.Date;

@Data
public class UserDto {

    private Integer id;
    private String name;
    private String password;
    private String contactNumber;
    private String email;
    private Date birthDate;
    private String role;


    public UserDto(Integer id, String name, String password, String contactNumber, String email, Date birthDate, String role ) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.contactNumber = contactNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.role = role;
    }

}
