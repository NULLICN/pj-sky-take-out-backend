package xyz.nullicn.skytakeserver.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Override
    public void exportExcel(HttpServletResponse response) {
        LocalDate endDate = LocalDate.now();
        LocalDate beginDate = endDate.minusMonths(1);

        String begin = beginDate.toString();
        String end = endDate.toString();

        TurnoverReportVO turnover = getTurnoverStatistics(begin, end);
        UserReportVO user = getUserStatistics(begin, end);
        OrderReportVO order = getOrdersStatistics(begin, end);

        String[] dateArr = turnover.getDateList().split(",");
        String[] turnoverArr = turnover.getTurnoverList().split(",");
        String[] newUserArr = user.getNewUserList().split(",");
        String[] totalOrderArr = order.getOrderCountList().split(",");
        String[] validOrderArr = order.getValidOrderCountList().split(",");

        try (XSSFWorkbook workbook = loadTemplate()) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            fillOverview(sheet, turnoverArr, newUserArr, order);
            fillDetail(sheet, dateArr, turnoverArr, validOrderArr, totalOrderArr, newUserArr);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String filename = URLEncoder.encode("运营数据报表_" + begin + "_" + end + ".xlsx",
                    StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("导出报表失败", e);
            throw new RuntimeException("导出报表失败", e);
        }
    }

    private XSSFWorkbook loadTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("运营数据报表模板.xlsx");
        return new XSSFWorkbook(resource.getInputStream());
    }

    private void fillOverview(XSSFSheet sheet, String[] turnoverArr, String[] newUserArr,
                               OrderReportVO order) {
        BigDecimal totalTurnover = BigDecimal.ZERO;
        for (String s : turnoverArr) {
            totalTurnover = totalTurnover.add(new BigDecimal(s));
        }
        int totalNewUsers = 0;
        for (String s : newUserArr) {
            totalNewUsers += Integer.parseInt(s);
        }

        BigDecimal avgPrice = order.getValidOrderCount() > 0
                ? totalTurnover.divide(BigDecimal.valueOf(order.getValidOrderCount()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        XSSFRow row4 = sheet.getRow(3);
        getOrCreateCell(row4, 2).setCellValue(totalTurnover.doubleValue());
        getOrCreateCell(row4, 4).setCellValue(order.getOrderCompletionRate());
        getOrCreateCell(row4, 6).setCellValue(totalNewUsers);

        XSSFRow row5 = sheet.getRow(4);
        getOrCreateCell(row5, 2).setCellValue(order.getValidOrderCount());
        getOrCreateCell(row5, 4).setCellValue(avgPrice.doubleValue());
    }

    private void fillDetail(XSSFSheet sheet, String[] dateArr, String[] turnoverArr,
                            String[] validOrderArr, String[] totalOrderArr, String[] newUserArr) {
        int dataStartRow = 7;
        int templateDataRows = 30;

        for (int i = 0; i < dateArr.length; i++) {
            XSSFRow row = i < templateDataRows
                    ? sheet.getRow(dataStartRow + i)
                    : sheet.createRow(dataStartRow + i);

            String date = dateArr[i];
            BigDecimal dayTurnover = new BigDecimal(turnoverArr[i]);
            int dayValidOrders = Integer.parseInt(validOrderArr[i]);
            int dayTotalOrders = Integer.parseInt(totalOrderArr[i]);
            int dayNewUsers = Integer.parseInt(newUserArr[i]);

            double dayCompletionRate = dayTotalOrders > 0
                    ? (double) dayValidOrders / dayTotalOrders : 0;
            double dayAvgPrice = dayValidOrders > 0
                    ? dayTurnover.divide(BigDecimal.valueOf(dayValidOrders), 2, RoundingMode.HALF_UP).doubleValue()
                    : 0;

            getOrCreateCell(row, 1).setCellValue(date);
            getOrCreateCell(row, 2).setCellValue(dayTurnover.doubleValue());
            getOrCreateCell(row, 3).setCellValue(dayValidOrders);
            getOrCreateCell(row, 4).setCellValue(dayCompletionRate);
            getOrCreateCell(row, 5).setCellValue(dayAvgPrice);
            getOrCreateCell(row, 6).setCellValue(dayNewUsers);
        }

        for (int i = dateArr.length; i < templateDataRows; i++) {
            XSSFRow row = sheet.getRow(dataStartRow + i);
            for (int col = 1; col <= 6; col++) {
                XSSFCell cell = row.getCell(col);
                if (cell != null) {
                    cell.setBlank();
                }
            }
        }
    }

    private XSSFCell getOrCreateCell(XSSFRow row, int col) {
        XSSFCell cell = row.getCell(col);
        return cell != null ? cell : row.createCell(col);
    }
}
