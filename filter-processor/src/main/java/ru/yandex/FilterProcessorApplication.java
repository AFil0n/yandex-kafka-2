package ru.yandex;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilterProcessorApplication {

    public static void main(String[] args) {
        new FilterProcessorUtils().runFilterProcessor();
    }
}