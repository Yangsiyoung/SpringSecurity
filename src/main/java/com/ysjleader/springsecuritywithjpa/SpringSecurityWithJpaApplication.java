package com.ysjleader.springsecuritywithjpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SpringSecurityWithJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityWithJpaApplication.class, args);
	}

	/**
	 *
	 * UserDetailsService 상속받아 구현한 loadUserByUsername 에서 던져주는
	 * UserDetails 를 받아서 입력한 비밀번호와 DB 에 저장된 비밀번호를 비교할텐데
	 * 저장할때 인코딩을 했지만, 사용자가 로그인시에 입력한 비밀번호에 대해서는
	 * 인코딩되어 들어오지 않을텐데.. 분명히 Filter 에서 해당 PasswordEncoder Bean 을
	 * 사용할텐데 어디서 사용하는지 확인해보자
	 *
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
