package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品,同时插入对应的口味数据,需要操作两张表:dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void updateStatus(Integer status, List<Long> ids);
}
