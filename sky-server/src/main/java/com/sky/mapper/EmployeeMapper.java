package com.sky.mapper;

import com.sky.entity.Employee;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    void insertEmployee(Employee employee);

    List<Employee> queryPage(String name);

    /**
     * 修改密码
     */
    void updatePassword(Employee employee);
    @Select("select * from employee where id = #{id}")
    Employee selectById(Long id);
}
