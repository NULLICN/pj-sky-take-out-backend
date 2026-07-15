package xyz.nullicn.skytakeserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.dto.ShoppingCartDTO;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.ShoppingCart;
import xyz.nullicn.exception.BaseException;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.mapper.ShoppingCartMapper;
import xyz.nullicn.skytakeserver.service.ShoppingCartService;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();

        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();

        boolean hasDish = dishId != null && dishId > 0;
        boolean hasSetmeal = setmealId != null && setmealId > 0;

        if (hasDish && hasSetmeal) {
            throw new BaseException(MessageConstant.DISH_SETMEAL_INCOMPATIBLE);
        }
        if (!hasDish && !hasSetmeal) {
            throw new BaseException(MessageConstant.DISH_SETMEAL_EMPTY);
        }

        ShoppingCart.ShoppingCartBuilder builder = ShoppingCart.builder()
                .userId(userId)
                .number(1);

        if (hasDish) {
            Dish dish = dishMapper.getById(dishId);
            if (dish == null) {
                throw new BaseException(MessageConstant.DISH_NOT_EXISTS);
            }
            builder.name(dish.getName())
                   .dishId(dishId)
                   .dishFlavor(shoppingCartDTO.getDishFlavor() != null
                           ? shoppingCartDTO.getDishFlavor() : "")
                   .amount(dish.getPrice())
                   .image(dish.getImage());
        } else {
            Setmeal setmeal = setmealMapper.getById(setmealId);
            if (setmeal == null) {
                throw new BaseException(MessageConstant.SETMEAL_NOT_EXISTS);
            }
            builder.name(setmeal.getName())
                   .setmealId(setmealId)
                   .amount(setmeal.getPrice())
                   .image(setmeal.getImage());
        }

        shoppingCartMapper.insert(builder.build());
    }
}
