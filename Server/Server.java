package Server;

import rmi.IStudentService;
import rmi.StudentService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            // Tạo RMI Registry trên cổng 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("✅ RMI Registry created at port 1099");

            // Tạo service quản lý sinh viên
            IStudentService service = new StudentService();

            // Đăng ký service với tên "StudentService"
            Naming.rebind("rmi://localhost:1099/StudentService", service);
            System.out.println("🚀 Server started and StudentService bound.");

        } catch (Exception e) {
            System.out.println("❌ Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
