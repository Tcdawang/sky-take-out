package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.EmployeeIsExistException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码比对
        //若用户名存在 将前端传来的密码进行加密登陆后 与数据库的psw进行比较
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        //3、返回实体对象
        return employee;
    }

    @Override
    public void insertEmployee(EmployeeDTO employeeDTO) {
        //更具用户名查询 该用户名是否存在
        Employee employee = employeeMapper.getByUsername(employeeDTO.getUsername());
        if (employee != null){
            throw new EmployeeIsExistException(MessageConstant.EMPLOYEE_IS_EXIST);
        }
        //如果不存在则创建实体类 为其赋值 如果一个一个调用set方法会很麻烦 可以使用spring提供的属性拷贝的工具类BeanUtil
        Employee newEmp = new Employee();
        BeanUtils.copyProperties(employeeDTO, newEmp);
        //再未剩余属性赋值
        //密码默认为123456 并进行加密存储
        newEmp.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置状态
        newEmp.setStatus(StatusConstant.ENABLE);
        //通过ThreadLocal获取id
        newEmp.setCreateUser(BaseContext.getCurrentId());
        newEmp.setUpdateUser(BaseContext.getCurrentId());
        //调用mapper方法添加数据
        employeeMapper.insertEmployee(newEmp);
    }

}
