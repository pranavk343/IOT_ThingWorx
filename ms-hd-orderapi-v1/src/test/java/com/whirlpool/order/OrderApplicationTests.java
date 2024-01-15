package com.whirlpool.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OrderApplicationTests {

	@Test
	void testMain() {
		HDOrderApplication.main(new String[] {});
		assertTrue(true);
	}

}
