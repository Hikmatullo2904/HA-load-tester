package uz.hikmatullo.loadtesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoadTestingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadTestingApplication.class, args);
    }

}
