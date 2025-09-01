package tobyspring.splearn.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MemberTest {

	@Test
	void createMember() {
		var member = new Member("cjl2076@naver.com", "jaeryang", "secret");

		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
	}
}
