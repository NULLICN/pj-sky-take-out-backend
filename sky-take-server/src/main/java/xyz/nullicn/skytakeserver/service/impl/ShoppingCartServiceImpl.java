package xyz.nullicn.skytakeserver.service.impl;

import cn.hutool.ai.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.ShoppingCart;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.mapper.ShoppingCartMapper;
import xyz.nullicn.skytakeserver.service.DishService;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.skytakeserver.service.ShoppingCartService;
import xyz.nullicn.utils.JwtUtil;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(ShoppingCartDTO shoppingCartDTO) {
        // 通过jwt校验得到用户id
        Long userId = BaseContext.getCurrentId();

        // 判断本次是添加菜品还是套餐
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();

        boolean hasDish = dishId != null && dishId > 0;
        boolean hasSetmeal = setmealId != null && setmealId > 0;

        if (hasDish && hasSetmeal) {
            throw new RuntimeException(MessageConstant.DISH_SETMEAL_INCOMPATIBLE);
        }
        if (!hasDish && !hasSetmeal) {
            throw new RuntimeException(MessageConstant.DISH_SETMEAL_EMPTY);
        }

        // 查询对应菜品或套餐其余数据
        Dish dish = null;
        Setmeal setmeal = null;

        if(hasDish) {
            dish = dishMapper.getById(dishId);
            if(dish == null) {
                throw new RuntimeException(MessageConstant.DISH_NOT_EXISTS);
            }
        }

        if(hasSetmeal) {
            setmeal = setmealMapper.getById(setmealId);
            if(setmeal == null) {
                throw new RuntimeException(MessageConstant.SETMEAL_NOT_EXISTS);
            }
        }

        // 构建ShoppingCart对象并写入数据库
        ShoppingCart goods = new ShoppingCart();
        goods.setUserId(userId);


        if (dish != null) {
            goods.setName(dish.getName());
            goods.setDishId(dishId);
            goods.setDishFlavor(shoppingCartDTO.getDishFlavor());
            goods.setNumber(1);
            goods.setAmount(dish.getPrice());
            goods.setImage(dish.getImage());
        }

        if (setmeal != null) {
            goods.setName(setmeal.getName());
            goods.setDishId(setmealId);
            goods.setNumber(1);
            goods.setAmount(setmeal.getPrice());
            goods.setImage(setmeal.getImage());
        }

        shoppingCartMapper.insert(goods);
    }
}
