package xyz.nullicn.skytakeserver;

import cn.hutool.core.bean.BeanUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.DefaultTypedTuple;
import xyz.nullicn.entity.Dish;
import xyz.nullicn.entity.Employee;
import xyz.nullicn.entity.Setmeal;
import xyz.nullicn.skytakeserver.mapper.DishMapper;
import xyz.nullicn.skytakeserver.mapper.EmployeeMapper;
import xyz.nullicn.skytakeserver.mapper.SetmealMapper;
import xyz.nullicn.skytakeserver.service.DishService;
import xyz.nullicn.skytakeserver.service.EmployeeService;
import xyz.nullicn.skytakeserver.service.SetmealService;
import xyz.nullicn.utils.PasswordUtil;
import xyz.nullicn.vo.SetmealVO;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@SpringBootTest
class SkyTakeServerApplicationTests {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishMapper dishMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void generateEncodePassword() {
        String pas = PasswordUtil.hashPassword("123456");
        System.out.println("pas: " + pas);
    }

    @Test
    void searchEmployeeTest() {
        System.out.println(employeeService.getEmployee(1));

    }

    @Test
    void getSetmealByIdTest() {
        Setmeal setmeal = setmealMapper.getById(33L);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtil.copyProperties(setmeal, setmealVO);
        System.out.println(setmeal);
        System.out.println(setmealVO);
    }

    @Test
    void testRedisTemplate() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("str1", "hello nullicn");
        // 目前配置的序列化器不能直接读取通过控制台方式设定的值
        String str1 = (String)valueOperations.get("str1");
        System.out.println(str1);

        Employee employee = employeeService.getEmployee(1);
        valueOperations.set("employee", employee);
        Employee str2 = (Employee) valueOperations.get("employee");
        System.out.println(str2.getPassword());
    }

    @Test
    void testHash() {
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        stringObjectObjectHashOperations.put("hash100", "name", "nullicn");
        stringObjectObjectHashOperations.put("hash100", "age", "22");
        String name = (String) stringObjectObjectHashOperations.get("hash100", "name");
        String age = (String) stringObjectObjectHashOperations.get("hash100", "age");
        System.out.printf("name: %s, age: %s\n", name, age);

        stringObjectObjectHashOperations.put("hash100", "data", "100");
        stringObjectObjectHashOperations.delete("hash100", "data");

        Set<Object> keys = stringObjectObjectHashOperations.keys("hash100");
        System.out.println("keys: " + keys);

        List<Object> Values = stringObjectObjectHashOperations.values("hash100");
        System.out.println("Values: " + Values);

    }

    @Test
    void testList() {
        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();
        stringObjectListOperations.leftPushAll("list2", "v1", "v2", "v3");
        stringObjectListOperations.leftPush("list2", "v4");
        List<Object> list = stringObjectListOperations.range("list2", 0, -1);
        System.out.println("list2: " + list);

        Long list2Size = stringObjectListOperations.size("list2");
        System.out.println("list2Size: " + list2Size);


    }

    @Test
    void testSet() {
        SetOperations<String, Object> stringObjectSetOperations = redisTemplate.opsForSet();
        stringObjectSetOperations.add("set1", "a", "b", "c", "d");
        stringObjectSetOperations.add("set2", "d", "e", "f", "g");

        Set<Object> set1 = stringObjectSetOperations.members("set1");
        System.out.println("set1: " + set1);

        Long set2Size = stringObjectSetOperations.size("set1");
        System.out.println("set2Size: " + set2Size);

        Set<Object> intersect = stringObjectSetOperations.intersect("set1", "set2");
        System.out.println("intersect: " + intersect);

        Set<Object> union = stringObjectSetOperations.union("set1", "set2");
        System.out.println("union: " + union);
    }

    @Test
    void testZSet() {
        ZSetOperations<String, Object> stringObjectZSetOperations = redisTemplate.opsForZSet();

        // 方式二：通过 TypedTuple 添加（单个）
        stringObjectZSetOperations.add("zset1", "a", 1);

        // 方式二：批量添加
        Set<TypedTuple<Object>> tuples = new java.util.HashSet<>();
        tuples.add(new DefaultTypedTuple<>("b", 2.0));
        tuples.add(new DefaultTypedTuple<>("c", 3.0));
        stringObjectZSetOperations.add("zset1", tuples);

        Set<Object> zset1 = stringObjectZSetOperations.range("zset1", 0, -1);

        System.out.println("zset1: " + zset1);

        stringObjectZSetOperations.remove("zset1", "a");
        Set<Object> zset2 = stringObjectZSetOperations.range("zset1", 0, -1);

        System.out.println("zset2: " + zset2);
    }

    // ==================== 缓存访问示例 ====================

    @Test
    void testCacheAside() {
        long id = 1L;
        String cacheKey = "cache:employee:" + id;

        // 1. 先查 Redis 缓存
        Employee employee = (Employee) redisTemplate.opsForValue().get(cacheKey);

        if (employee != null) {
            System.out.println(">>> 缓存命中，直接返回 → " + employee.getName());
            return;
        }

        // 2. 缓存未命中，查数据库
        System.out.println(">>> 缓存未命中，查数据库...");
        employee = employeeMapper.findById(id);

        if (employee == null) {
            System.out.println(">>> 数据库中也不存在");
            return;
        }

        // 3. 数据库查到结果，写入 Redis 缓存（30分钟过期）
        redisTemplate.opsForValue().set(cacheKey, employee, Duration.ofMinutes(30));
        System.out.println(">>> 已写入缓存 → " + employee.getName());
    }

    /**
     * 生产环境标准方式：@Cacheable 注解
     * 调用两次，观察控制台输出 —— 第二次不会执行方法体
     */
    @Test
    void testCacheable() {
        long id = 1L;

        System.out.println("=== 第 1 次调用（缓存未命中，会查库） ===");
        Employee e1 = employeeService.getEmployeeCached(id);
        System.out.println("结果: " + e1.getName());

        System.out.println("=== 第 2 次调用（命中缓存，不查库） ===");
        Employee e2 = employeeService.getEmployeeCached(id);
        System.out.println("结果: " + e2.getName());
    }

    @Test
    void testDishCache() {
        dishService.listWithFlavor(17L);
    }

    @Test
    void testGetDish() {
        Dish dish = dishMapper.getById(46L);
        System.out.println(dish);
    }
}