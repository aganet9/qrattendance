package ru.chsu.qrattendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//java -jar target/qrattendance-0.1.0.jar --keycloak.url=http://localhost:8081
@SpringBootApplication
public class QrAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrAttendanceApplication.class, args);
    }

}
