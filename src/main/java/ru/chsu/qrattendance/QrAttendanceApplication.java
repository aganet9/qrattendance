package ru.chsu.qrattendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//java -jar target/qrattendance-0.0.1-SNAPSHOT.jar --keycloak.url=http://localhost:9091
@SpringBootApplication
public class QrAttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrAttendanceApplication.class, args);
    }

}
