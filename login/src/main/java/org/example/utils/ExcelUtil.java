package org.example.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Excel工具类
 *
 * @author example
 */
public class ExcelUtil {

    /**
     * 导出Excel
     *
     * @param list     数据列表
     * @param fileName 文件名
     * @param clazz    数据类型
     * @param response HTTP响应
     */
    public static <T> void exportExcel(List<T> list, String fileName, Class<T> clazz, HttpServletResponse response) {
        try {
            // 创建工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(fileName);

            // 获取字段
            Field[] fields = clazz.getDeclaredFields();
            
            // 创建标题行
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            for (int i = 0; i < fields.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(getFieldDisplayName(fields[i]));
                cell.setCellStyle(headerStyle);
            }

            // 创建数据行
            CellStyle dataStyle = createDataStyle(workbook);
            for (int i = 0; i < list.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                T item = list.get(i);
                
                for (int j = 0; j < fields.length; j++) {
                    Cell cell = dataRow.createCell(j);
                    fields[j].setAccessible(true);
                    Object value = fields[j].get(item);
                    setCellValue(cell, value);
                    cell.setCellStyle(dataStyle);
                }
            }

            // 自动调整列宽
            for (int i = 0; i < fields.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            setResponseHeaders(response, fileName);

            // 写入响应
            workbook.write(response.getOutputStream());
            workbook.close();
            
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建标题样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 获取字段显示名称
     */
    private static String getFieldDisplayName(Field field) {
        String fieldName = field.getName();
        // 简单的字段名转换，可以根据需要扩展
        switch (fieldName) {
            case "dictId": return "字典ID";
            case "dictName": return "字典名称";
            case "dictType": return "字典类型";
            case "status": return "状态";
            case "createBy": return "创建者";
            case "createTime": return "创建时间";
            case "updateBy": return "更新者";
            case "updateTime": return "更新时间";
            case "remark": return "备注";
            case "dictCode": return "字典编码";
            case "dictLabel": return "字典标签";
            case "dictValue": return "字典键值";
            case "dictSort": return "字典排序";
            case "cssClass": return "样式属性";
            case "listClass": return "表格回显样式";
            case "isDefault": return "是否默认";
            default: return fieldName;
        }
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDateTime) {
            LocalDateTime dateTime = (LocalDateTime) value;
            cell.setCellValue(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 设置响应头
     */
    private static void setResponseHeaders(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        String encodedFileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedFileName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
}