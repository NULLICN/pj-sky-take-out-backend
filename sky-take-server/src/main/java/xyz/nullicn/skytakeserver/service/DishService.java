package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.vo.DishVO;

import java.util.List;

public interface DishService {
    void addWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteBatch(List<Long> ids);

    void updateWithFlavor(DishDTO dishDTO);

    DishVO getDByIdWithFlavor(Long id);
}
