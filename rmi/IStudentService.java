package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Student;

public interface IStudentService extends Remote {
    // CRUD cơ bản
    void addStudent(Student s) throws RemoteException;
    List<Student> getAllStudents() throws RemoteException;
    Student getStudentById(int id) throws RemoteException;
    void deleteStudent(int id) throws RemoteException;
    void updateStudent(Student s) throws RemoteException;

    // Tìm theo mã SV
    Student getStudentByMasv(String masv) throws RemoteException;
    void deleteStudent(String masv) throws RemoteException;

    //Tìm kiếm có bộ lọc
    List<Student> searchStudents(String masv, String hoten, String gioitinh, String tentinh, String khoa, String lop) throws RemoteException;

    //Lấy sinh viên theo lớp
    List<Student> getStudentsByClass(String tenLop) throws RemoteException;

    //Điểm danh
    void saveAttendance(String masv, String status, java.util.Date date) throws RemoteException;
    List<Object[]> getAttendanceByClass(String tenLop, java.util.Date date) throws RemoteException;
}