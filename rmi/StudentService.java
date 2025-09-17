package rmi;

import dao.StudentDAO;
import model.Student;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class StudentService extends UnicastRemoteObject implements IStudentService {
    private StudentDAO dao;

    public StudentService() throws RemoteException {
        super();
        dao = new StudentDAO();
    }

    @Override
    public void addStudent(Student s) throws RemoteException {
        dao.addStudent(s);
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        return dao.getAllStudents();
    }

    @Override
    public Student getStudentById(int id) throws RemoteException {
        return dao.getStudentById(id);
    }
    
    @Override
    public void deleteStudent(int id) throws RemoteException {
        dao.deleteStudent(id);
    }
    
    @Override
    public void updateStudent(Student s) throws RemoteException {
        dao.updateStudent(s);
    }
}