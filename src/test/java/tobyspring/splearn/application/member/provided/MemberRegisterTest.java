package tobyspring.splearn.application.member.provided;

import static org.assertj.core.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import tobyspring.splearn.SplearnTestConfiguration;
import tobyspring.splearn.domain.member.DuplicateEmailException;
import tobyspring.splearn.domain.member.DuplicateProfileException;
import tobyspring.splearn.domain.member.Member;
import tobyspring.splearn.domain.member.MemberFixture;
import tobyspring.splearn.domain.member.MemberInfoUpdateRequest;
import tobyspring.splearn.domain.member.MemberRegisterRequest;
import tobyspring.splearn.domain.member.MemberStatus;

@SpringBootTest
@Transactional //오탐: 트랜잭션 AOP와 달리 Spring TestContext 프레임워크가 테스트 메서드 단위로 트랜잭션을 시작/롤백해 주므로, 프록시가 필요없다.
@Import(SplearnTestConfiguration.class)
	// @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) //파라미터를 어떻게 가져올지
record MemberRegisterTest(MemberRegister memberRegister, EntityManager entityManager) {

	@Test
	void register() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());

		System.out.println(member);

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
		checkValidation(new MemberRegisterRequest("cjl2076@naver.com", "cjl", "secret"));
		checkValidation(new MemberRegisterRequest("cjl2076@naver.com", "jaeryang", "secret"));
		checkValidation(new MemberRegisterRequest("cjl2076naver.com", "jaeryang", "longsecret"));
	}

	@Test
	void activate() {
		Member member = registerMember();

		member = memberRegister.activate(member.getId());
		entityManager.flush(); //실제로 쿼리가 반영되는지 확인을 위해

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getActivatedAt()).isNotNull();
	}

	@Test
	void deactivate() {
		Member member = registerMember();

		memberRegister.activate(member.getId());
		entityManager.flush(); //실제로 쿼리가 반영되는지 확인을 위해
		entityManager.clear();

		member = memberRegister.deactivate(member.getId());
		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
	}

	private Member registerMember() {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest());
		entityManager.flush();
		entityManager.clear();
		return member;
	}

	private Member registerMember(String email) {
		Member member = memberRegister.register(MemberFixture.createMemberRegisterRequest(email));
		entityManager.flush();
		entityManager.clear();
		return member;
	}

	@Test
	void updateInfo() {
		Member member = registerMember();

		memberRegister.activate(member.getId());
		entityManager.flush(); //실제로 쿼리가 반영되는지 확인을 위해
		entityManager.clear();

		var request = new MemberInfoUpdateRequest("cjl0701", "jaeryang001", "자기소개");
		member = memberRegister.updateInfo(member.getId(), request);

		assertThat(member.getDetail().getProfile().address()).isEqualTo("jaeryang001");

	}

	@Test
	void updateInfoFail() {
		Member member = registerMember(); //람다 안에서 사용할 때는 변수의 값을 복사하는 방식이므로, effective final이어야 한다.
		memberRegister.activate(member.getId());
		memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("cjl0701", "jaeryang001", "자기소개"));

		Member member2 = registerMember("cjl2077@naver.com");
		memberRegister.activate(member2.getId());
		entityManager.flush(); //실제로 쿼리가 반영되는지 확인을 위해
		entityManager.clear();

		// member2는 기존의 member와 같은 프로필 주소를 사용할 수 없다.
		assertThatThrownBy(() ->
			memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("cjl2076", "jaeryang001", "자기소개"))
		).isInstanceOf(DuplicateProfileException.class);

		// 다른 프로필 주소로는 변경 가능
		memberRegister.updateInfo(member2.getId(), new MemberInfoUpdateRequest("cjl2076", "jaeryang002", "자기소개"));

		//기존 프로필 주소로 바꾸는 것도 가능
		memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("cjl2076", "jaeryang001", "자기소개"));

		// 프로필 주소를 제거하는 것도 가능
		memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("cjl2076", "", "자기소개"));

		// 프로필 주소 중복은 허용하지 않음
		assertThatThrownBy(() ->
			memberRegister.updateInfo(member.getId(), new MemberInfoUpdateRequest("cjl2076", "jaeryang002", "자기소개"))
		).isInstanceOf(DuplicateProfileException.class);
	}

	private void checkValidation(MemberRegisterRequest invalid) {
		assertThatThrownBy(() -> memberRegister.register(invalid))
			.isInstanceOf(ConstraintViolationException.class);
	}
}
