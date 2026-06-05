package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.nullicn.entity.DishFlavor;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量新增菜品口味
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}
