package com.wesavefp.wesaveFP;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@SpringBootApplication
public class WesaveFpApplication {

	public static void main(String[] args) {
		SpringApplication.run(WesaveFpApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StudentRepository repository, MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address(
					"England",
					"London",
					"NE9"
			);
			String email = "jamilaahmed@gmail.com";
			Student student = new Student(
					"Jamila",
					"Ahmed",
					email,
					Gender.FEMALE,
					address,
					List.of("Computer Science, Maths"),
					BigDecimal.TEN,
					LocalDateTime.now()
			);

			Query query = new Query();
			query.addCriteria(Criteria.where("email").is(email));

			List<Student> students = mongoTemplate.find(query, Student.class);

			if(students.size() > 1) {
				throw new IllegalStateException(
						"found many students with email " + email
				);
			}

			if (students.isEmpty()) {
				System.out.println("Inserting student " + students);
				repository.insert(student);
			} else {
				System.out.println(student + " already exists");
			}

			repository.insert(student);
		};
	}
}
