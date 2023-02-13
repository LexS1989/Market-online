package ru.skypro.homework.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long fileSize;
    private String mediaType;
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "ads_id")
    private Ads ads;
}
