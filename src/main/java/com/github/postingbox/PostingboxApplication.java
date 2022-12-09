package com.github.postingbox;

import com.github.postingbox.domain.BlogInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = BlogInfo.class)
public class PostingboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostingboxApplication.class, args);
    }
}
