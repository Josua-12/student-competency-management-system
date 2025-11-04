package com.competency.SCMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StudentCompetencyManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentCompetencyManagementSystemApplication.class, args);
	}

}
