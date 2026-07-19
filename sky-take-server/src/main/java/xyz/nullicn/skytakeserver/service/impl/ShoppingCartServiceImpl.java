package xyz.nullicn.skytakeserver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

import java.time.Duration;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    private static final String SHOPPING_CART_KEY = "shoppingCart";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

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

        // 添加新商品后删除旧的购物车缓存
        deleteUserShoppingCartCache(userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        String key = buildKey(userId);

        List<ShoppingCart> cached = (List<ShoppingCart>) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        List<ShoppingCart> list = shoppingCartMapper.list(userId);
        if(list != null && !(list.isEmpty())){
            redisTemplate.opsForValue().set(key, list, CACHE_TTL);
        }
        return list;
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();

        // 清空购物车同时也清空缓存
        deleteUserShoppingCartCache(userId);
        shoppingCartMapper.clean(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sub(ShoppingCartDTO shoppingCartDTO) {
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

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .dishId(dishId)
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .setmealId(setmealId)
                .build();

        ShoppingCart existing = shoppingCartMapper.getByCondition(shoppingCart);
        if (existing == null) {
            throw new BaseException(MessageConstant.SHOPPING_CART_ITEM_NOT_FOUND);
        }

        if (existing.getNumber() <= 1) {
            shoppingCartMapper.deleteByCondition(shoppingCart);
        } else {
            shoppingCartMapper.sub(shoppingCart);
        }

        deleteUserShoppingCartCache(userId);
    }

    private String buildKey(Long userId) {
        return SHOPPING_CART_KEY + ":" + userId;
    }

    private void deleteUserShoppingCartCache(Long userId) {
        redisTemplate.delete(buildKey(userId));
    }

}
