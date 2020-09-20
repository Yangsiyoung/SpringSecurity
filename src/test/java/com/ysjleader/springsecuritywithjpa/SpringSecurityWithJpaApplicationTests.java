package com.ysjleader.springsecuritywithjpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpringSecurityWithJpaApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void testBcryptPasswordEncoderSuitableStrengthParameter() {

		LocalDateTime startTime = LocalDateTime.now();
		LocalDateTime expectedEndTime = startTime.plusSeconds(1);

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(14);
		String encodedPassword = bCryptPasswordEncoder.encode("myPassword");

		assertTrue(bCryptPasswordEncoder.matches("myPassword", encodedPassword));
		assertTrue(LocalDateTime.now().isAfter(expectedEndTime));

	}

}
