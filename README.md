# FTP-Client-Server
FTP Client/Server


## I.	Giới thiệu sơ lược về chủ đề
### a.	Mục tiêu
-	Công cụ cho phép gửi nhận/file qua mạng theo mô hình client/server
-	Hỗ trợ đa luồng
-	Hỗ trợ 2 giao diện thư mục client và server và cho phép kéo thả
### b.	Kết quả đã đạt được
-	Cho phép gửi, nhận file
-	Server giới hạn nhận file với 1 số đuôi (vd: .txt, .mp3, .wav, .docx, .jpg, .jpeg)
-	Lưu nhật ký ra log
-	Sử dụng giao diện
-	Hỗ trợ đa luồng
-	Việc gửi file có cả tính toán thời gian
-	Cho phép hủy tiến trình đang thực hiện gửi/ nhận file
### c.	Hạn chế, hướng phát triển
-	Gửi 1 file bằng nhiều luồng
-	Không thông báo cho server khi hủy tiến trình gửi/ nhận


 
# User Guide				
## Setup
### Run UserCtr.java để khởi tạo sẵn tài khoản và mặt khẩu đã thiết lập 
### Run ServerFrm.java và bấm start. Server chạy thành công ở port 2312 đã thiết lập sẵn
![image](https://user-images.githubusercontent.com/52437817/199625496-e78c2c53-a7a2-4da3-bf35-dc0683f35e0e.png)
### Run ClientFrm.java và đăng nhập ip, tài khoản và mật khẩu đã thiết lập. Đăng nhập thành công.
![image](https://user-images.githubusercontent.com/52437817/199625767-8fe57001-5f11-4217-8d35-9e9511cfcd90.png)
### Click choose file hoặc kéo file vào nút để chọn file cần gửi và nhập đường dẫn đích và tên file cần lưu và bấm send file
![image](https://user-images.githubusercontent.com/52437817/199628424-48954e4e-b52b-4ea6-a401-e9e9eda7f10f.png)
