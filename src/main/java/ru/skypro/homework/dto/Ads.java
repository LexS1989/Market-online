package ru.skypro.homework.dto;

import lombok.Data;

import java.util.List;

@Data
public class Ads {

    private int author;
    private List<String> image;
    private int pk;
    private int price;
    private String title;
}
