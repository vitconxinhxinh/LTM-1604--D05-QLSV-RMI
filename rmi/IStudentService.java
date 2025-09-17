package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Student;

public interface IStudentService extends Remote {
    void addStudent(Student s) throws RemoteException;
    List<Student> getAllStudents() throws RemoteException;
    Student getStudentById(int id) throws RemoteException;
    void deleteStudent(int id) throws RemoteException;
    void updateStudent(Student s) throws RemoteException;
}