package com.inn.webshop.com.inn.webshop.data.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "bill")
public class BillEntity {

    private static final Long serialVersionUid = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private Integer uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;
}
