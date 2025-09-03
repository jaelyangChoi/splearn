package tobyspring.splearn.application.provided;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.DuplicateEmailException;
import tobyspring.splearn.domain.Member;
import tobyspring.splearn.domain.MemberFixture;
import tobyspring.splearn.domain.MemberRegisterRequest;
import tobyspring.splearn.domain.MemberStatus;

@SpringBootTest
@Transactional
@Import(SplearnTestConfiguration.class)
// @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) //파라미터를 어떻게 가져올지
public record MemberRegisterTest(
	MemberRegister memberRegister,
	EntityManager entityManager) { //오탐: Spring TestContext 프레임워크가 테스트 메서드 단위로 트랜잭션을 시작/롤백해 주므로, 프록시가 필요없다.

	@Test
	void register() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

		assertThat(member.getId()).isNotNull();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
	}

	@Test
	void duplicateEmailFail() {
		memberRegister.register(MemberFixture.createMemberRegisterRequest());

		assertThatThrownBy(() -> memberRegister.register(MemberFixture.createMemberRegisterRequest()))
			.isInstanceOf(DuplicateEmailException.class);
	}

	@Test
	void memberRegisterRequestFail() {
		extracted(new MemberRegisterRequest("cjl2076@naver.com", "cjl", "secret"));
		extracted(new MemberRegisterRequest("cjl2076@naver.com", "jaeryang", "secret"));
		extracted(new MemberRegisterRequest("cjl2076naver.com", "jaeryang", "longsecret"));
	}

	@Test
	void activate() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
		entityManager.flush();
		entityManager.clear();

		member = memberRegister.activate(member.getId());
		entityManager.flush(); //실제로 쿼리가 반영되는지 확인을 위해

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);

	}

	private void extracted(MemberRegisterRequest invalid) {
		assertThatThrownBy(() -> memberRegister.register(invalid))
			.isInstanceOf(ConstraintViolationException.class);
	}
}
