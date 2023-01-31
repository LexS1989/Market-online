package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String phone;
    private String regDate;//в спецификации String, или применить LocalDateTime спросить???

    //Стоит ли добавить Role и Password или вынести в отдельную таблицу ???

    @OneToOne(mappedBy = "user")
    private Avatar avatar;

    @OneToMany(mappedBy = "user")
    private Collection<Ads> adsList;
}

