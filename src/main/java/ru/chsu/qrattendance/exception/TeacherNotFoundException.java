package ru.chsu.qrattendance.exception;

public class TeacherNotFoundException extends RuntimeException {
    public TeacherNotFoundException(String email) {
        super("Преподаватель с почтой: " + email + " не найден");
    }
}
