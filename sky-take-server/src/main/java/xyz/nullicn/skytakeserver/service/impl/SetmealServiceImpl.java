package xyz.nullicn.skytakeserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.entity.Category;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.SetmealDish;
import xyz.nullicn.skytakeserver.mapper.CategoryMapper;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.vo.SetmealVO;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        // 根据分类id查询该分类下的所有菜品
        List<Dish> dishes = dishMapper.getByCategoryId(setmealDTO.getCategoryId());

        // 构建套餐实体并插入
        Setmeal setmeal = Setmeal.builder()
                .categoryId(setmealDTO.getCategoryId())
                .name(setmealDTO.getName())
                .price(setmealDTO.getPrice())
                .status(setmealDTO.getStatus())
                .description(setmealDTO.getDescription())
                .image(setmealDTO.getImage())
                .build();
        setmealMapper.insert(setmeal);

        // 批量插入套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(sd -> sd.setSetmealId(setmeal.getId()));
            setmealMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public SetmealVO getById(Long id) {
        // 查询套餐
        Setmeal setmeal = setmealMapper.getById(id);
        // 查询套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealMapper.getSetmealDishesBySetmealId(id);
        // 获取套餐分类名称
        String categoryName = categoryMapper.getById(setmeal.getCategoryId()).getName();
        // 把菜品塞入到套餐菜品属性中
        SetmealVO setmealVO = SetmealVO.builder()
                .id(setmeal.getId())
                .categoryId(setmeal.getCategoryId())
                .categoryName(categoryName)
                .name(setmeal.getName())
                .price(setmeal.getPrice())
                .status(setmeal.getStatus())
                .description(setmeal.getDescription())
                .image(setmeal.getImage())
                .updateTime(setmeal.getUpdateTime())
                .setmealDishes(setmealDishes)
                .build();
        return setmealVO;
    }
}
