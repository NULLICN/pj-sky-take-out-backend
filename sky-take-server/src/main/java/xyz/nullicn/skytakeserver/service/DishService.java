package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.result.PageResult;

public interface DishService {
    void addWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
}
