package xyz.nullicn.skytakeserver.service;

import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.vo.SetmealVO;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    SetmealVO getById(Long id);
}
