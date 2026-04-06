package io.group32;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.Map;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner testUpload(Cloudinary cloudinary) {
//        return args -> {
//            try {
//                File file = new File("C:\\Users\\alexg\\Downloads\\bella_moncler.png");
//
//                if (file.exists()) {
//                    Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
//                    System.out.println("sucess: " + uploadResult.get("url"));
//                }
//            } catch (Exception exception) {
//                System.out.println(exception.toString());
//            }
//        };
//    }
}