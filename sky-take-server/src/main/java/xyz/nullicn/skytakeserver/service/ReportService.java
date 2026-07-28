package xyz.nullicn.skytakeserver.service;

import jakarta.servlet.http.HttpServletResponse;
import xyz.nullicn.vo.OrderReportVO;
import xyz.nullicn.vo.SalesTop10ReportVO;
import xyz.nullicn.vo.TurnoverReportVO;
import xyz.nullicn.vo.UserReportVO;

public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(String begin, String end);

    UserReportVO getUserStatistics(String begin, String end);

    OrderReportVO getOrdersStatistics(String begin, String end);

    SalesTop10ReportVO getSalesTop10(String begin, String end);

    void exportExcel(HttpServletResponse response);

}
