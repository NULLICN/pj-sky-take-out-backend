package xyz.nullicn.skytakeserver.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.SetmealDish;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    List<Long> getSetmealIdsByDishId(List<Long> dishIds);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    void insertBatch(List<SetmealDish> setmealDishes);
}
