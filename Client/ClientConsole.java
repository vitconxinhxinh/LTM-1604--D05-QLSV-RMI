package Client;

import rmi.IStudentService;
import model.Student;

import java.rmi.Naming;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class ClientConsole {
    public static void main(String[] args) {
        try {
            // Kết nối tới RMI Registry
            IStudentService service = (IStudentService) Naming.lookup("rmi://localhost:1099/StudentService");
            Scanner sc = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\n===== MENU QUẢN LÝ SINH VIÊN =====");
                System.out.println("1. Thêm sinh viên");
                System.out.println("2. Xem danh sách");
                System.out.println("3. Tìm sinh viên theo ID");
                System.out.println("4. Xóa sinh viên");
                System.out.println("5. Thoát");
                System.out.print("Chọn: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                case 1: // Thêm sinh viên
                    // KHÔNG cần nhập ID và MSSV nữa vì DB tự sinh
                    System.out.print("Nhập tên: ");
                    String name = sc.nextLine();

                    System.out.print("Nhập ngày sinh (yyyy-mm-dd): ");
                    String birthStr = sc.nextLine();
                    Date birthdate = Date.valueOf(birthStr);

                    System.out.print("Nhập Class ID (ví dụ: 1 = CNTT1, 2 = CNTT2): ");
                    int classId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Nhập GPA: ");
                    double gpa = sc.nextDouble();
                    sc.nextLine();

                    System.out.print("Nhập Email: ");
                    String email = sc.nextLine();

                    System.out.print("Nhập SĐT: ");
                    String phone = sc.nextLine();

                    System.out.print("Nhập Address ID (ví dụ: 1 = Hà Nội, 2 = TP.HCM): ");
                    int addressId = sc.nextInt();
                    sc.nextLine();

                    // ID và MSSV để null → DB tự sinh
                    Student s = new Student(0, null, name, birthdate, classId, gpa, email, phone, addressId);
                    s.setEmail(email);
                    s.setPhone(phone);
                    s.setAddressId(addressId);

                    service.addStudent(s);
                    System.out.println(">> Đã thêm thành công!");
                    break;



                    case 2: // Xem danh sách
                        List<Student> list = service.getAllStudents();
                        if (list.isEmpty()) {
                            System.out.println(">> Chưa có sinh viên nào!");
                        } else {
                            for (Student st : list) {
                                System.out.println(st);
                            }
                        }
                        break;

                    case 3: // Tìm theo ID
                        System.out.print("Nhập ID: ");
                        int sid = sc.nextInt();
                        Student found = service.getStudentById(sid);
                        if (found != null) {
                            System.out.println(">> " + found);
                        } else {
                            System.out.println(">> Không tìm thấy!");
                        }
                        break;

                    case 4: // Xóa sinh viên
                        System.out.print("Nhập ID: ");
                        int did = sc.nextInt();
                        service.deleteStudent(did);
                        System.out.println(">> Đã xóa (nếu tồn tại)!");
                        break;

                    case 5:
                        System.out.println("Thoát...");
                        break;

                    default:
                        System.out.println("Lựa chọn sai!");
                }
            } while (choice != 5);

            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
