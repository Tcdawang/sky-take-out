package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeEditPswDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.*;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeePageVO;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Slf4j
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
        if (employee != null) {
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

    @Override
    public PageResult<EmployeePageVO> queryPage(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult<EmployeePageVO> pr = new PageResult<>();
        //开启分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        //调用mapper方法
        List<Employee> employees = employeeMapper.queryPage(employeePageQueryDTO.getName());

        //通过stream流将employee 都转换成 employeePageVO
        List<EmployeePageVO> employeePageVOS = employees.stream().map(employee -> {
            EmployeePageVO employeePageVO = new EmployeePageVO();
            BeanUtils.copyProperties(employee, employeePageVO);
            return employeePageVO;
        }).toList();
        log.info("数据为：{}", employeePageVOS);
        //获取查询总数
        pr.setTotal(employeePageVOS.size());
        //获取查询结果
        pr.setRecords(employeePageVOS);
        return pr;
    }

    @Override
    public void updatePassword(EmployeeEditPswDTO employeeEditPswDTO) {
        log.info("前端密码值:{}", employeeEditPswDTO);
        //获取前端传来的参数值
        String newPassword = employeeEditPswDTO.getNewPassword();
        String oldPassword = employeeEditPswDTO.getOldPassword();

        //更具ThreadLocal获取此时人的id
        Long currentId = BaseContext.getCurrentId();
        //根据id查询当前用户是否存在
        Employee employee = employeeMapper.selectById(currentId);
        //判断原始密码是否正确
        if (!DigestUtils.md5DigestAsHex(oldPassword.getBytes()).equals(employee.getPassword())){
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_EDIT_FAILED_OLDPSW);
        }

        //判断两次修改的密码是否一致
        if (newPassword.equals(oldPassword)){
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_EDIT_FAILED_SAME);
        }


        //加密密码
        String newPsw = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        //创建员工对象
        Employee newEmployee = new Employee();
        newEmployee.setId(currentId);
        newEmployee.setPassword(newPsw);
        newEmployee.setUpdateUser(currentId);
        //调用mapper修改密码
        employeeMapper.updatePassword(newEmployee);
    }

    @Override
    public void stopAndStart(Integer status, Long id) {
        log.info("前端传回来的状态值和id值为:{},{}",status,id);
        Employee employee = new Employee();
        Long currentId = BaseContext.getCurrentId();
        employee.setStatus(status);
        employee.setId(id);
        employee.setUpdateUser(currentId);
        employeeMapper.stopAndStart(employee);
    }

    @Override
    public Employee selectById(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        Long currentId = BaseContext.getCurrentId();
        employee.setUpdateUser(currentId);
        employeeMapper.updateEmp(employee);
    }
}
