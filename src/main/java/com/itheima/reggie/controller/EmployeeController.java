package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //MD5加密处理密码
        //
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if(emp == null){
            return R.error("登录失败");
        }

        if(!password.equals(emp.getPassword())){
            return R.error("登录失败");
        }

        if(emp.getStatus() == 0){
            return R.error("账号禁用");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
       //log.info(employee.toString());
       employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
       employee.setCreateTime(LocalDateTime.now());
       employee.setUpdateTime(LocalDateTime.now());
       employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
       employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
       employeeService.save(employee);
       return R.success("新增员工成功");
    }
}
