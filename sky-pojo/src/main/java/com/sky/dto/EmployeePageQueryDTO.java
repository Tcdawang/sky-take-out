package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

@Data
/**
 * 更具姓名查询员工并进行分页
 */
public class EmployeePageQueryDTO implements Serializable {

    //员工姓名
    private String name;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;

}
