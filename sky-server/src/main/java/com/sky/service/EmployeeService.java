package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeEditPswDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.vo.EmployeePageVO;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param
     */
    void insertEmployee(EmployeeDTO employeeDTO);

    PageResult<EmployeePageVO> queryPage(EmployeePageQueryDTO employeePageQueryDTO);

    void updatePassword(EmployeeEditPswDTO employeeEditPswDTO);
}
