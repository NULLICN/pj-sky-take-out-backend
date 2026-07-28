package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.nullicn.result.Result;
import xyz.nullicn.skytakeserver.service.ReportService;
import xyz.nullicn.vo.OrderReportVO;
import xyz.nullicn.vo.SalesTop10ReportVO;
import xyz.nullicn.vo.TurnoverReportVO;
import xyz.nullicn.vo.UserReportVO;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@RequiredArgsConstructor
@Tag(name= "数据统计")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额统计")
    public Result<TurnoverReportVO> turnoverReportVOResult(@RequestParam String begin, @RequestParam String end) {
        log.info("查询营业额统计，日期范围：{} 至 {}", begin, end);
        TurnoverReportVO vo = reportService.getTurnoverStatistics(begin, end);
        return Result.success(vo);
    }

    @GetMapping("/userStatistics")
    @Operation(summary = "用户数统计")
    public Result<UserReportVO> userReportVOResult(@RequestParam String begin, @RequestParam String end) {
        log.info("查询用户数统计，日期范围：{} 至 {}", begin, end);
        UserReportVO vo = reportService.getUserStatistics(begin, end);
        return Result.success(vo);
    }

    @GetMapping("/ordersStatistics")
    @Operation(summary = "订单数统计")
    public Result<OrderReportVO> ordersReportVOResult(@RequestParam String begin, @RequestParam String end) {
        log.info("查询订单数统计，日期范围：{} 至 {}", begin, end);
        OrderReportVO vo = reportService.getOrdersStatistics(begin, end);
        return Result.success(vo);
    }

    @GetMapping("/top10")
    @Operation(summary = "TOP10商品榜单")
    public Result<SalesTop10ReportVO> top10ReportVOResult(@RequestParam String begin, @RequestParam String end) {
        log.info("查询TOP10商品榜单，日期范围：{} 至 {}", begin, end);
        SalesTop10ReportVO vo = reportService.getSalesTop10(begin, end);
        return Result.success(vo);
    }

    @GetMapping("/export")
    @Operation(summary = "导出数据报表")
    public void exportExcel(HttpServletResponse response) {
        reportService.exportExcel(response);
    }
}
