package xyz.nullicn.skytakeserver.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.dto.SetmealDTO;
import xyz.nullicn.dto.SetmealPageQueryDTO;
import xyz.nullicn.entity.Category;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.entity.SetmealDish;
import xyz.nullicn.exception.BaseException;
import xyz.nullicn.exception.DeletionNotAllowedException;
import xyz.nullicn.result.PageResult;
import xyz.nullicn.skytakeserver.mapper.CategoryMapper;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.vo.DishVO;
import xyz.nullicn.vo.SetmealVO;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        // 构建套餐实体并插入
        Setmeal setmeal = Setmeal.builder()
                .categoryId(setmealDTO.getCategoryId())
                .name(setmealDTO.getName())
                .price(setmealDTO.getPrice())
                .status(setmealDTO.getStatus())
                .description(setmealDTO.getDescription())
                .image(setmealDTO.getImage())
                .build();
        setmealMapper.insert(setmeal);

        // 批量插入套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(sd -> sd.setSetmealId(setmeal.getId()));
            setmealMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public SetmealVO getById(Long id) {
        // 查询套餐
        Setmeal setmeal = setmealMapper.getById(id);
        // 查询套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealMapper.getSetmealDishesBySetmealId(id);
        // 获取套餐分类名称
        String categoryName = categoryMapper.getById(setmeal.getCategoryId()).getName();
        // 把菜品塞入到套餐菜品属性中
        SetmealVO setmealVO = SetmealVO.builder()
                .id(setmeal.getId())
                .categoryId(setmeal.getCategoryId())
                .categoryName(categoryName)
                .name(setmeal.getName())
                .price(setmeal.getPrice())
                .status(setmeal.getStatus())
                .description(setmeal.getDescription())
                .image(setmeal.getImage())
                .updateTime(setmeal.getUpdateTime())
                .setmealDishes(setmealDishes)
                .build();
        return setmealVO;
    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        int page = setmealPageQueryDTO.getPage();
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(page, pageSize);
        Page<SetmealVO> aPage = setmealMapper.pageQuery(setmealPageQueryDTO);

        long total = aPage.getTotal();
        List<SetmealVO> setmealVOList = aPage.getResult();

        return new PageResult(total, setmealVOList);
    }

    @Override
    public void status(int status, Long id) {
        if (id <= 0) {
            throw new BaseException("套餐ID必须为正整数");
        }
        if (status != 0 && status != 1) {
            throw new BaseException("状态值必须为0或1");
        }

        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();

        setmealMapper.update(setmeal);

    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 创建setmeal对象并更新数据库
        Setmeal setmeal = Setmeal.builder()
                .categoryId(setmealDTO.getCategoryId())
                .id(setmealDTO.getId())
                .name(setmealDTO.getName())
                .price(setmealDTO.getPrice())
                .status(setmealDTO.getStatus())
                .description(setmealDTO.getDescription())
                .image(setmealDTO.getImage())
                .build();
        setmealMapper.update(setmeal);
        // 获取setmealDish内容并存入数据库
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealMapper.deleteDishesBySetmealId(setmeal.getId());
            setmealDishes.forEach(d -> d.setSetmealId(setmeal.getId()));
            setmealMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    @Transactional
    public void deleteBatchByIds(List<Long> ids) {
        //检查套餐是否已起售
        for (Long id : ids) {
            int status = setmealMapper.getById(id).getStatus();
            if(status == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        // 删除对应id的套餐
        setmealMapper.deleteBatchByIds(ids);
        // 删除套餐下的菜品（遍历）
        for (Long id : ids) {
            setmealMapper.deleteDishesBySetmealId(id);
        }
    }
}
