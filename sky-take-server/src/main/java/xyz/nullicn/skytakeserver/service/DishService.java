package xyz.nullicn.skytakeserver.service;

import jakarta.validation.Valid;
import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.skytakeserver.annotation.AutoFill;

import java.util.List;

public interface DishService {
    void addWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void editDish(DishDTO dishDTO);
}
