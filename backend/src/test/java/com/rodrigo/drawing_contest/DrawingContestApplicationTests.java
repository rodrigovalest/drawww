package com.rodrigo.drawing_contest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DrawingContestApplicationTests {

	@Test
	public void runDrawingContestApplication_ShouldStart() {
		DrawingContestApplication.main(new String[]{});
	}
}
