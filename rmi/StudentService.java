package rmi;

import dao.StudentDAO;
import model.Student;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Date;

public class StudentService extends UnicastRemoteObject implements IStudentService {
    private StudentDAO dao;

    public StudentService() throws RemoteException {
        super();
        dao = new StudentDAO();
    }

    @Override
    public void addStudent(Student s) throws RemoteException {
        dao.addStudent(s); // Gọi trực tiếp, không ép kiểu
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        return dao.getAllStudents();
    }

    @Override
    public Student getStudentByMasv(String masv) throws RemoteException {
        return dao.getStudentByMasv(masv); // Gọi trực tiếp
    }

    @Override
    public void deleteStudent(String masv) throws RemoteException {
        dao.deleteStudent(masv); // Gọi trực tiếp
    }

    @Override
    public void updateStudent(Student s) throws RemoteException {
        dao.updateStudent(s); // Gọi trực tiếp
    }

    @Override
    public List<Student> getStudentsByClass(String tenLop) throws RemoteException {
        return dao.getStudentsByClass(tenLop); // Gọi trực tiếp
    }

    @Override
    public void saveAttendance(String masv, String status, Date date) throws RemoteException {
        dao.saveAttendance(masv, status, date); // Gọi trực tiếp
    }

    @Override
    public List<Object[]> getAttendanceByClass(String tenLop, Date date) throws RemoteException {
        return dao.getAttendanceByClass(tenLop, date); // Gọi trực tiếp
    }

    @Override
    public Student getStudentById(int id) throws RemoteException {
        return dao.getStudentById(id); // Gọi trực tiếp
    }

    @Override
    public void deleteStudent(int id) throws RemoteException {
        dao.deleteStudent(id); // Gọi trực tiếp
    }

    @Override
    public List<Student> searchStudents(String masv, String hoten, String gioitinh, String tentinh, String tenkhoa, String tenlop)
            throws RemoteException {
        return dao.searchStudents(masv, hoten, gioitinh, tentinh, tenkhoa, tenlop); // Gọi trực tiếp
    }
}