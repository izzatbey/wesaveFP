package com.wesavefp.wesaveFP;

import com.wesavefp.wesaveFP.helper.JsonToObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class WesaveFpApplication {



	public static void main(String[] args) throws IOException {
		SpringApplication.run(WesaveFpApplication.class, args);

	}
}
