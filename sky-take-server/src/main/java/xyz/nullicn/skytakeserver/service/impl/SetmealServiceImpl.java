package xyz.nullicn.skytakeserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.SetmealDish;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.service.SetmealService;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

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
}
