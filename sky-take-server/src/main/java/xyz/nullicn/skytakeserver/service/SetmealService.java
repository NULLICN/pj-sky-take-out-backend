package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.dto.SetmealPageQueryDTO;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.vo.DishItemVO;
import xyz.nullicn.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    SetmealVO getSetmealById(Long id);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    void status(int status, Long id);

    void update(SetmealDTO setmealDTO);

    void deleteBatchByIds(List<Long> ids);

    List<Setmeal> getSetmealsByCategoryId(Long categoryId);

    List<DishItemVO> getDishesBySetmealId(Long id);
}
