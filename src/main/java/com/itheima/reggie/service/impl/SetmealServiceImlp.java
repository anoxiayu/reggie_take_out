package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImlp extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存菜品基本信息 操作setmeal,执行insert操作 -> 保存套餐和菜品的关联信息,操作setmeal_dish,执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((d) -> {
            d.setSetmealId(setmealDto.getId());
            return d;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //查询套餐状态,确定是否可以删除 -> 如果不能删除,抛出业务异常 ->可以删除的话 先删除套餐表中的数据 ->删除关系表中的数据
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐售卖中,不能删除");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper2);

    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        // 构造更新条件
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();

        // 1. 设置要筛选的 ID 范围 (WHERE id IN (...))
        updateWrapper.in(Setmeal::getId, ids);

        // 2. 设置要更新的字段值 (SET status = status)
        updateWrapper.set(Setmeal::getStatus, status);

        // 3. 执行更新
        setmealService.update(updateWrapper);
    }
}
