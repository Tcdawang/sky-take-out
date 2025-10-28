package com.sky.dto;

import lombok.Data;

/**
 * 修改用户密码
 */
@Data
public class EmployeeEditPswDTO {
    private String newPassword;
    private String oldPassword;
}
