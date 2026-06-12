package xyz.nullicn.skytakeserver.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import xyz.nullicn.dto.DishDTO;
import xyz.nullicn.dto.DishPageQueryDTO;
import xyz.nullicn.dto.EmployeePageQueryDTO;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.enumeration.OperationType;
import xyz.nullicn.skytakeserver.annotation.AutoFill;
import xyz.nullicn.vo.DishVO;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * from dish where id = #{id}")
    Dish getById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
}
