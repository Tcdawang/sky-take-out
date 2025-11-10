package com.sky.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class DishStatus implements Serializable {
    private Long id;
    private Integer status;
}   
