package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.CategoryException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    //TODO 在分页查询之前添加条件判断如果此时查询没有带条件值 我们就正常进行分页查询 如果有就将页面重置为1
    public PageResult<Category> queryPage(CategoryPageQueryDTO categoryPageQueryDTO) {
        //创建分页查询的对象
        PageResult<Category> pr = new PageResult<>();
        //开启分页查询
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //调用mapper
        Page<Category> categories = (Page<Category>)categoryMapper.
                queryPage(categoryPageQueryDTO.getName(), categoryPageQueryDTO.getType());

        log.info("查询出来的数据集合，{}", categories);
        pr.setTotal(categories.getTotal());
        pr.setRecords(categories.getResult());

        return pr;
    }

    @Override
    public void insertCategory(CategoryDTO categoryDTO) {
        //根据分类名称查询该分类是否存在
        Category category = categoryMapper.selectByName(categoryDTO.getName());
        log.info("新增分类名为:{}", categoryDTO.getName());
        if (category != null){
            throw new CategoryException(MessageConstant.CATEGORY_IS_EXIST);
        }
        Category insertCategory = new Category();
        BeanUtils.copyProperties(categoryDTO, insertCategory);
        //获取当前登陆人的id
        Long currentId = BaseContext.getCurrentId();
        insertCategory.setStatus(StatusConstant.ENABLE);
        insertCategory.setCreateUser(currentId);
        insertCategory.setUpdateUser(currentId);

        log.info("新增分类的具体信息：{}", insertCategory);

        categoryMapper.insertCategory(insertCategory);
    }

    @Override
    public Category selectByName(String name) {
        return categoryMapper.selectByName(name);
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        Long currentId = BaseContext.getCurrentId();
        category.setUpdateUser(currentId);
        log.info("修改后的分类：{}", category);
        categoryMapper.updateCategory(category);
    }

    @Override
    public List<Category> selectByType(Integer type) {
        return categoryMapper.selectByType(type);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(status);
        Long currentId = BaseContext.getCurrentId();
        category.setUpdateUser(currentId);
        categoryMapper.updateCategory(category);
    }

    @Override
    public void delete(Long id) {
        //查询该id下有多少的菜品
        Integer dishNum = dishMapper.selectByCategoryId(id);
        if (dishNum > 0){
            throw new CategoryException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //再查询该id下有多少个套餐
        Integer setmealNum = setmealMapper.selectByCategoryId(id);
        if (setmealNum > 0){
            throw new CategoryException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        //如果都没有再删除该分类
        categoryMapper.delete(id);
    }
}
