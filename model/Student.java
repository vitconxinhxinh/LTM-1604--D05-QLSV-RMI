package model;

import java.io.Serializable;
import java.sql.Date;

public class Student implements Serializable {
    private int id;
    private String mssv;
    private String name;
    private Date birthdate;
    private int classId;
    private double gpa;
    private String email;
    private String phone;
    private int addressId;

    private String className;   

    // Địa chỉ tách riêng
    private String city;
    private String district;
    private String ward;

    private String status;

    public Student() {}

    public Student(int id, String mssv, String name, Date birthdate,
                   int classId, double gpa, String email, String phone, int addressId) {
        this.id = id;
        this.mssv = mssv;
        this.name = name;
        this.birthdate = birthdate;
        this.classId = classId;
        this.gpa = gpa;
        this.email = email;
        this.phone = phone;
        this.addressId = addressId;
    }

    // ===== Getter / Setter =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMssv() { return mssv; }
    public void setMssv(String mssv) { this.mssv = mssv; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getBirthdate() { return birthdate; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Ghép địa chỉ thành 1 chuỗi để hiển thị nhanh
    public String getAddressDetail() {
        StringBuilder sb = new StringBuilder();
        if (ward != null && !ward.isEmpty()) sb.append(ward);
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(district);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(city);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", mssv='" + mssv + '\'' +
                ", name='" + name + '\'' +
                ", birthdate=" + birthdate +
                ", className='" + className + '\'' +
                ", gpa=" + gpa +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", ward='" + ward + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
