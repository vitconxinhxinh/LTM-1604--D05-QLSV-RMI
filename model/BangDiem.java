package model;

import java.io.Serializable;

public class BangDiem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String masv;
    private String mamh;
    private double diemCC;
    private double diemQT;
    private double diemCK;
    private double diemTK;
    private String magv;

    public BangDiem() {}

    public BangDiem(String masv, String mamh, double diemCC, double diemQT, double diemCK, double diemTK, String magv) {
        this.masv = masv;
        this.mamh = mamh;
        this.diemCC = diemCC;
        this.diemQT = diemQT;
        this.diemCK = diemCK;
        this.diemTK = diemTK;
        this.magv = magv;
    }

    // Constructor tự động tính điểm tổng kết
    public BangDiem(String masv, String mamh, double diemCC, double diemQT, double diemCK, String magv) {
        this.masv = masv;
        this.mamh = mamh;
        this.diemCC = diemCC;
        this.diemQT = diemQT;
        this.diemCK = diemCK;
        this.diemTK = 0.1 * diemCC + 0.2 * diemQT + 0.7 * diemCK;
        this.magv = magv;
    }
    public double getDiemCC() { return diemCC; }
    public void setDiemCC(double diemCC) { this.diemCC = diemCC; }

    public String getMasv() { return masv; }
    public void setMasv(String masv) { this.masv = masv; }

    public String getMamh() { return mamh; }
    public void setMamh(String mamh) { this.mamh = mamh; }

    public double getDiemQT() { return diemQT; }
    public void setDiemQT(double diemQT) { this.diemQT = diemQT; }

    public double getDiemCK() { return diemCK; }
    public void setDiemCK(double diemCK) { this.diemCK = diemCK; }

    public double getDiemTK() { return diemTK; }
    public void setDiemTK(double diemTK) { this.diemTK = diemTK; }
    // Tính lại điểm tổng kết khi thay đổi điểm quá trình hoặc điểm cuối kỳ
    public void updateDiemTK() {
    this.diemTK = 0.1 * diemCC + 0.2 * diemQT + 0.7 * diemCK;
    }

    public String getMagv() { return magv; }
    public void setMagv(String magv) { this.magv = magv; }
}
