package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
/**
 * 添加员工 与 修改员工
 */
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;

}
