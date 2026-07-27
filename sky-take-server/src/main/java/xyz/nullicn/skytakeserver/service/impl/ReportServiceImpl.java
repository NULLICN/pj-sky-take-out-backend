package xyz.nullicn.skytakeserver.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.nullicn.context.BaseContext;
import xyz.nullicn.entity.Orders;
import xyz.nullicn.skytakeserver.mapper.OrderDetailMapper;
import xyz.nullicn.skytakeserver.mapper.OrderMapper;
import xyz.nullicn.skytakeserver.mapper.UserMapper;
import xyz.nullicn.skytakeserver.service.ReportService;
import xyz.nullicn.vo.OrderReportVO;
import xyz.nullicn.vo.SalesTop10ReportVO;
import xyz.nullicn.vo.TurnoverReportVO;
import xyz.nullicn.vo.UserReportVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(String begin, String end) {
        LocalDate beginDate = LocalDate.parse(begin);
        LocalDate endDate = LocalDate.parse(end);

        List<Map<String, Object>> results = orderMapper.getTurnoverByDate(
                beginDate, endDate.plusDays(1), Orders.COMPLETED, BaseContext.getCurrentId());

        Map<LocalDate, BigDecimal> turnoverMap = results.stream()
                .collect(Collectors.toMap(
                        m -> ((java.sql.Date) m.get("order_date")).toLocalDate(),
                        m -> (BigDecimal) m.get("turnover")
                ));

        StringBuilder dateList = new StringBuilder();
        StringBuilder turnoverList = new StringBuilder();

        for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (dateList.length() > 0) {
                dateList.append(",");
                turnoverList.append(",");
            }
            dateList.append(date);
            turnoverList.append(turnoverMap.getOrDefault(date, BigDecimal.ZERO));
        }

        return TurnoverReportVO.builder()
                .dateList(dateList.toString())
                .turnoverList(turnoverList.toString())
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(String begin, String end) {
        LocalDate beginDate = LocalDate.parse(begin);
        LocalDate endDate = LocalDate.parse(end);
        Long employeeId = BaseContext.getCurrentId();

        int totalUsers = userMapper.countUsersBeforeDate(beginDate, employeeId);

        List<Map<String, Object>> results = userMapper.getNewUserCountByDate(
                beginDate, endDate.plusDays(1), employeeId);

        Map<LocalDate, Integer> newUserMap = results.stream()
                .collect(Collectors.toMap(
                        m -> ((java.sql.Date) m.get("user_date")).toLocalDate(),
                        m -> ((Number) m.get("new_users")).intValue()
                ));

        StringBuilder dateList = new StringBuilder();
        StringBuilder totalUserList = new StringBuilder();
        StringBuilder newUserList = new StringBuilder();

        for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int newUsers = newUserMap.getOrDefault(date, 0);
            totalUsers += newUsers;

            if (dateList.length() > 0) {
                dateList.append(",");
                totalUserList.append(",");
                newUserList.append(",");
            }
            dateList.append(date);
            totalUserList.append(totalUsers);
            newUserList.append(newUsers);
        }

        return UserReportVO.builder()
                .dateList(dateList.toString())
                .totalUserList(totalUserList.toString())
                .newUserList(newUserList.toString())
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(String begin, String end) {
        LocalDate beginDate = LocalDate.parse(begin);
        LocalDate endDate = LocalDate.parse(end);
        Long employeeId = BaseContext.getCurrentId();

        List<Map<String, Object>> results = orderMapper.getOrderCountByDate(
                beginDate, endDate.plusDays(1), Orders.COMPLETED, employeeId);

        // 处理原始db Map数据 转换为java中能使用的数据
        Map<LocalDate, int[]> countMap = results.stream()
                .collect(Collectors.toMap(
                        m -> ((java.sql.Date) m.get("order_date")).toLocalDate(),
                        m -> new int[]{
                                ((Number) m.get("total_orders")).intValue(),
                                ((Number) m.get("valid_orders")).intValue()
                        }
                ));

        StringBuilder dateList = new StringBuilder();
        StringBuilder orderCountList = new StringBuilder();
        StringBuilder validOrderCountList = new StringBuilder();
        int totalOrderCount = 0;
        int validOrderCount = 0;

        // 对处理后的db Map进行数据统计
        for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 每次先拿取一条数据
            int[] counts = countMap.getOrDefault(date, new int[]{0, 0});
            int dailyTotal = counts[0];
            int dailyValid = counts[1];
            totalOrderCount += dailyTotal;
            validOrderCount += dailyValid;

            if (dateList.length() > 0) {
                dateList.append(",");
                orderCountList.append(",");
                validOrderCountList.append(",");
            }
            dateList.append(date);
            orderCountList.append(dailyTotal);
            validOrderCountList.append(dailyValid);
        }

        double orderCompletionRate = totalOrderCount > 0
                ? (double) validOrderCount / totalOrderCount
                : 0.0;

        return OrderReportVO.builder()
                .dateList(dateList.toString())
                .orderCountList(orderCountList.toString())
                .validOrderCountList(validOrderCountList.toString())
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(String begin, String end) {
        LocalDate beginDate = LocalDate.parse(begin);
        LocalDate endDate = LocalDate.parse(end);

        List<Map<String, Object>> results = orderDetailMapper.getTop10Sales(
                beginDate, endDate.plusDays(1), Orders.COMPLETED, BaseContext.getCurrentId());

        StringBuilder nameList = new StringBuilder();
        StringBuilder numberList = new StringBuilder();

        for (Map<String, Object> row : results) {
            if (nameList.length() > 0) {
                nameList.append(",");
                numberList.append(",");
            }
            nameList.append((String) row.get("name"));
            numberList.append(((Number) row.get("total_number")).intValue());
        }

        return SalesTop10ReportVO.builder()
                .nameList(nameList.toString())
                .numberList(numberList.toString())
                .build();
    }
}
