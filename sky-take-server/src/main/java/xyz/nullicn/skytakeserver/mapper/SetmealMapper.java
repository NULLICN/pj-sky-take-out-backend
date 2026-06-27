package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.nullicn.dto.SetmealPageQueryDTO;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.SetmealDish;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;
import xyz.nullicn.vo.SetmealVO;

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

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    List<SetmealDish> getSetmealDishesBySetmealId(Long setmealId);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
