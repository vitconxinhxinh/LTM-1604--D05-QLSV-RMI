package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {
    /**
     * Xuất bảng điểm ra file Excel đúng định dạng: MSSV, Họ tên, Điểm CC, Điểm QT, Điểm CK, Điểm TK
     * @param data List<Object[]>: mỗi phần tử là 1 dòng gồm [masv, hoten, diemCC, diemQT, diemCK, diemTK]
     * @param filePath Đường dẫn file Excel muốn lưu
     */
    public static void exportStudentScoreToExcel(java.util.List<Object[]> data, String filePath) throws Exception {
        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("BangDiem");

        // Header
        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = {"MSSV", "Họ tên", "Điểm CC", "Điểm QT", "Điểm CK", "Điểm TK"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // Data
        for (int i = 0; i < data.size(); i++) {
            Object[] rowObj = data.get(i);
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
            for (int j = 0; j < headers.length; j++) {
                row.createCell(j).setCellValue(rowObj[j] != null ? rowObj[j].toString() : "");
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }
    /**
     * Xuất bảng điểm ra file Excel, có thêm tên môn học và tên lớp ở đầu file
     * @param data List<Object[]>: mỗi phần tử là 1 dòng gồm [masv, hoten, diemCC, diemQT, diemCK, diemTK]
     * @param filePath Đường dẫn file Excel muốn lưu
     * @param tenMonHoc Tên môn học
     * @param tenLop Tên lớp
     */
    public static void exportScoreToExcelWithInfo(java.util.List<Object[]> data, String filePath, String tenMonHoc, String tenLop) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("BangDiem");

        // Thông tin lớp và môn học
        Row infoRow1 = sheet.createRow(0);
        infoRow1.createCell(0).setCellValue("Lớp: " + (tenLop != null ? tenLop : ""));
        Row infoRow2 = sheet.createRow(1);
        infoRow2.createCell(0).setCellValue("Môn học: " + (tenMonHoc != null ? tenMonHoc : ""));

        // Header
        Row header = sheet.createRow(3);
        header.createCell(0).setCellValue("Mã SV");
        header.createCell(1).setCellValue("Họ tên");
        header.createCell(2).setCellValue("Điểm CC");
        header.createCell(3).setCellValue("Điểm QT");
        header.createCell(4).setCellValue("Điểm CK");
        header.createCell(5).setCellValue("Điểm TK");

        // Data
        for (int i = 0; i < data.size(); i++) {
            Object[] rowObj = data.get(i);
            Row row = sheet.createRow(i + 4);
            for (int j = 0; j < 6; j++) {
                row.createCell(j).setCellValue(rowObj[j] != null ? rowObj[j].toString() : "");
            }
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }
    /**
     * Xuất bảng điểm ra file Excel
     * @param data List<Object[]>: mỗi phần tử là 1 dòng gồm [masv, mamh, diemqt, diemck, diemtk, magv]
     * @param filePath Đường dẫn file Excel muốn lưu
     * @throws Exception nếu có lỗi IO
     */
    public static void exportScoreToExcel(List<Object[]> data, String filePath) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("BangDiem");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Mã SV");
        header.createCell(1).setCellValue("Mã môn");
        header.createCell(2).setCellValue("Điểm QT");
        header.createCell(3).setCellValue("Điểm CK");
        header.createCell(4).setCellValue("Điểm TK");
        header.createCell(5).setCellValue("Mã GV");

        // Data
        for (int i = 0; i < data.size(); i++) {
            Object[] rowObj = data.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < 6; j++) {
                row.createCell(j).setCellValue(rowObj[j] != null ? rowObj[j].toString() : "");
            }
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }
    /**
     * Xuất dữ liệu điểm danh ra file Excel
     * @param data List<Object[]>: mỗi phần tử là 1 dòng gồm [masv, hoten, trangthai]
     * @param filePath Đường dẫn file Excel muốn lưu
     * @throws Exception nếu có lỗi IO
     */
    public static void exportAttendanceToExcel(List<Object[]> data, String filePath) throws Exception {
        System.out.println("[ExcelExporter] Bắt đầu ghi file: " + filePath);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("DiemDanh");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Mã SV");
        header.createCell(1).setCellValue("Họ tên");
        header.createCell(2).setCellValue("Trạng thái");

        // Data
        for (int i = 0; i < data.size(); i++) {
            Object[] rowObj = data.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(rowObj[0] != null ? rowObj[0].toString() : "");
            row.createCell(1).setCellValue(rowObj[1] != null ? rowObj[1].toString() : "");
            row.createCell(2).setCellValue(rowObj[2] != null ? rowObj[2].toString() : "");
        }

        // Auto-size columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            System.out.println("[ExcelExporter] Ghi file thành công: " + filePath);
        }
        workbook.close();
    }
}
