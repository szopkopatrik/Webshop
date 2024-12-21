package com.inn.webshop.com.inn.webshop.data.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(
        name = "CategoryEntity.getAllCategory",
        query = "SELECT c FROM CategoryEntity c"
)

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "category")
public class CategoryEntity implements Serializable {

    private static final Long serialVersionUid = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;
}
