package in.ashu.practice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import in.ashu.practice.controller.HelloController;

@SpringBootTest
class SpringBootAuthJwtApplicationTests {

	@Autowired
	private HelloController helloController;

	@Test
	void contextLoads() {
		assertThat(helloController).isNotNull();
	}

}
