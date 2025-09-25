package model;

import java.io.Serializable;

public class Student implements Serializable {
    private String masv;      // Mã sinh viên (PK)
    private String hoten;     // Họ tên
    private int tuoi;         // Tuổi
    private String email;     // Email
    private String gioitinh;  // Giới tính (M/F)
    private String sdt;       // Số điện thoại
    private String matinh;    // Mã tỉnh (FK)
    private String malop;     // Mã lớp (FK)
    private String makhoa;    // Mã khoa (FK)

    // ===== Trường bổ sung để hiển thị (JOIN từ bảng khác) =====
    private String tenTinh;
    private String tenLop;
    private String tenKhoa;
    
    public Student() {
        // constructor mặc định, để DAO khởi tạo xong mới set từng field
    }


    public Student(String masv, String hoten, String gioitinh, int tuoi,
            String tenLop, String tenTinh) {
		 this.masv = masv;
		 this.hoten = hoten;
		 this.gioitinh = gioitinh;
		 this.tuoi = tuoi;
		 this.tenLop = tenLop;
		 this.tenTinh = tenTinh;
		}

    public Student(String masv, String hoten, int tuoi, String email, String gioitinh,
            String sdt, String matinh, String malop, String makhoa,
            String tenTinh, String tenLop, String tenKhoa) {
		 this.masv = masv;
		 this.hoten = hoten;
		 this.tuoi = tuoi;
		 this.email = email;
		 this.gioitinh = gioitinh;
		 this.sdt = sdt;
		 this.matinh = matinh;
		 this.malop = malop;
		 this.makhoa = makhoa;
		 this.tenTinh = tenTinh;
		 this.tenLop = tenLop;
		 this.tenKhoa = tenKhoa;
		}

    // ===== Getter / Setter =====
    public String getMasv() { return masv; }
    public void setMasv(String masv) { this.masv = masv; }

    public String getHoten() { return hoten; }
    public void setHoten(String hoten) { this.hoten = hoten; }

    public int getTuoi() { return tuoi; }
    public void setTuoi(int tuoi) { this.tuoi = tuoi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGioitinh() { return gioitinh; }
    public void setGioitinh(String gioitinh) { this.gioitinh = gioitinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getMatinh() { return matinh; }
    public void setMatinh(String matinh) { this.matinh = matinh; }

    public String getMalop() { return malop; }
    public void setMalop(String malop) { this.malop = malop; }

    public String getMakhoa() { return makhoa; }
    public void setMakhoa(String makhoa) { this.makhoa = makhoa; }

    // ===== Thông tin mở rộng =====
    public String getTenTinh() { return tenTinh; }
    public void setTenTinh(String tenTinh) { this.tenTinh = tenTinh; }

    public String getTenLop() { return tenLop; }
    public void setTenLop(String tenLop) { this.tenLop = tenLop; }

    public String getTenKhoa() { return tenKhoa; }
    public void setTenKhoa(String tenKhoa) { this.tenKhoa = tenKhoa; }

    @Override
    public String toString() {
        return "Student{" +
                "masv='" + masv + '\'' +
                ", hoten='" + hoten + '\'' +
                ", tuoi=" + tuoi +
                ", email='" + email + '\'' +
                ", gioitinh='" + gioitinh + '\'' +
                ", sdt='" + sdt + '\'' +
                ", matinh='" + matinh + '\'' +
                ", malop='" + malop + '\'' +
                ", makhoa='" + makhoa + '\'' +
                ", tenTinh='" + tenTinh + '\'' +
                ", tenLop='" + tenLop + '\'' +
                ", tenKhoa='" + tenKhoa + '\'' +
                '}';
    }
}
