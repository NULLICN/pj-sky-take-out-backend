package xyz.nullicn.skytakeserver.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.constant.StatusConstant;
import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.DishFlavor;
import xyz.nullicn.exception.BaseException;
import xyz.nullicn.exception.DeletionNotAllowedException;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.skytakeserver.mapper.DishFlavorMapper;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.service.DishService;
import xyz.nullicn.vo.DishVO;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void addWithFlavor(DishDTO dishDTO) {
        Dish dish = Dish.builder()
                .name(dishDTO.getName())
                .categoryId(dishDTO.getCategoryId())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .description(dishDTO.getDescription())
                .status(dishDTO.getStatus())
                .build();
        dishMapper.insert(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(f -> f.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {

        int page = dishPageQueryDTO.getPage();
        int pageSize = dishPageQueryDTO.getPageSize();

        PageHelper.startPage(page, pageSize);
        Page<DishVO> aPage = dishMapper.pageQuery(dishPageQueryDTO);

        long total = aPage.getTotal();
        List<DishVO> dishes = aPage.getResult();

        return new PageResult(total, dishes);

    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 检查菜品是否启用
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 检查菜品是否被关联至套餐
        List<Long> setmealids = setmealMapper.getSetmealIdsByDishId(ids);
        if(setmealids != null && setmealids.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        for(Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = Dish.builder()
                .id(dishDTO.getId())
                .name(dishDTO.getName())
                .categoryId(dishDTO.getCategoryId())
                .price(dishDTO.getPrice())
                .image(dishDTO.getImage())
                .description(dishDTO.getDescription())
                .status(dishDTO.getStatus())
                .build();
        dishMapper.update(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            dishFlavorMapper.deleteByDishId(dishDTO.getId());
            flavors.forEach(f -> f.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public DishVO getDByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        if(dish == null) {
            throw new BaseException(MessageConstant.DISH_NOT_EXISTS);
        }
        List<DishFlavor> flavors = dishFlavorMapper.getBydishId(dish.getId());
        DishVO dishVO = DishVO.builder()
                .id(dish.getId())
                .name(dish.getName())
                .categoryId(dish.getCategoryId())
                .price(dish.getPrice())
                .image(dish.getImage())
                .description(dish.getDescription())
                .status(dish.getStatus())
                .updateTime(dish.getUpdateTime())
                .flavors(flavors)
                .build();
        return dishVO;
    }

    @Override
    public List<Dish> getDishesByCategoryId(Long categoryId) {
        List<Dish> dishList = dishMapper.getByCategoryId(categoryId);
        return dishList;
    }

    @Override
    public void status(int status, Long id) {
        if (id <= 0) {
            throw new BaseException("菜品ID必须为正整数");
        }
        if (status != 0 && status != 1) {
            throw new BaseException("状态值必须为0或1");
        }

        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();

        dishMapper.update(dish);
    }
}
