package tobyspring.splearn.domain.member;

import static org.assertj.core.api.Assertions.*;
import static tobyspring.splearn.domain.member.MemberFixture.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {

	Member member;
	PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		passwordEncoder = createPasswordEncoder();

		member = Member.register(createMemberRegisterRequest(), passwordEncoder);
	}

	@Test
	void registerMember() {
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
		assertThat(member.getDetail().getRegisteredAt()).isNotNull();
	}

	@Test
	void activate() {
		assertThat(member.getDetail().getActivatedAt()).isNull();

		member.activate();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
		assertThat(member.getDetail().getActivatedAt()).isNotNull();

	}

	@Test
	void activateFail() {
		member.activate();

		assertThatThrownBy(member::activate).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void deactivate() {
		member.activate();

		member.deactivate();

		assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
		assertThat(member.getDetail().getDeactivatedAt()).isNotNull();
	}

	@Test
	void deactivateFail() {
		assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);

		member.activate();
		member.deactivate();

		assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void verifyPassword() {
		assertThat(member.verifyPassword("longsecret", passwordEncoder)).isTrue();
		assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
	}

	@Test
	void changePassword() {
		member.changePassword("verySecret", passwordEncoder);

		assertThat(member.verifyPassword("verySecret", passwordEncoder)).isTrue();
	}

	@Test
	void isActive() {
		assertThat(member.isActive()).isFalse();

		member.activate();

		assertThat(member.isActive()).isTrue();

		member.deactivate();

		assertThat(member.isActive()).isFalse();
	}

	@Test
	void invalidEmail() {
		assertThatThrownBy(
			() -> Member.register(
				createMemberRegisterRequest("invalid email"), passwordEncoder)
		).isInstanceOf(IllegalArgumentException.class);

		Member.register(createMemberRegisterRequest(), passwordEncoder);
	}

	@Test
	void updateInfo() {
		member.activate();

		var request = new MemberInfoUpdateRequest("cjl0701", "jaeryang001", "자기소개");
		member.updateInfo(request);

		assertThat(member.getNickname()).isEqualTo(request.nickname());
		assertThat(member.getDetail().getProfile().address()).isEqualTo(request.profileAddress());
		assertThat(member.getDetail().getIntroduction()).isEqualTo(request.introduction());
	}
}
