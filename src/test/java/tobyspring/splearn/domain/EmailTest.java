package tobyspring.splearn.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailTest {

	// record는 값들(component)로 equals를 만들어준다.
	@Test
	void equality() {
		var email1 = new Email("cjl2076@naver.com");
		var email2 = new Email("cjl2076@naver.com");

		assertThat(email1).isEqualTo(email2);
	}
}
