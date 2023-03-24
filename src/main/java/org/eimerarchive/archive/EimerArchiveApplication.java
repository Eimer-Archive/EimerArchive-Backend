package org.eimerarchive.archive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@ConfigurationPropertiesScan
public class EimerArchiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(EimerArchiveApplication.class, args);
    }
}