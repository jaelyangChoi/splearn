package tobyspring.splearn.application.provided;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static tobyspring.splearn.domain.MemberFixture.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import tobyspring.splearn.application.MemberService;
import tobyspring.splearn.application.required.EmailSender;
import tobyspring.splearn.application.required.MemberRepository;
import tobyspring.splearn.domain.Email;
import tobyspring.splearn.domain.Member;
import tobyspring.splearn.domain.MemberFixture;
import tobyspring.splearn.domain.MemberStatus;

//인터페이스를 기준으로 테스트하는 것이 원칙
class MemberRegisterManualTest {
	@Test
	void registerTestStub() {
		MemberRegister register = new MemberService(
			new MemberRepositoryStub(), new EmailSenderStub(), MemberFixture.createPasswordEncoder()
		);

		Member member = register.register(createMemberRegisterRequest());

		assertThat(member.getId()).isNotNull();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
	}

	@Test
	void registerTestMock() {
		EmailSenderMock emailSenderMock = new EmailSenderMock();
		MemberRegister register = new MemberService(
			new MemberRepositoryStub(), emailSenderMock, MemberFixture.createPasswordEncoder()
		);

		Member member = register.register(createMemberRegisterRequest());

		assertThat(member.getId()).isNotNull();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);

		assertThat(emailSenderMock.getTos()).hasSize(1);
		assertThat(emailSenderMock.getTos().getFirst()).isEqualTo(member.getEmail());
	}

	//실전에서는 mock을 직접 만들기 복잡해서, 라이브러리를 갖다 쓴다.
	@Test
	void registerTestMockito() {
		EmailSender emailSenderMock = Mockito.mock(EmailSender.class);
		MemberRegister register = new MemberService(
			new MemberRepositoryStub(), emailSenderMock, MemberFixture.createPasswordEncoder()
		);

		Member member = register.register(createMemberRegisterRequest());

		assertThat(member.getId()).isNotNull();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);

		Mockito.verify(emailSenderMock).send(eq(member.getEmail()), any(), any());
	}


	static class MemberRepositoryStub implements MemberRepository {
		@Override
		public Member save(Member member) {
			ReflectionTestUtils.setField(member, "id", 1L);
			return member;
		}

		@Override
		public Optional<Member> findByEmail(Email email) {
			return Optional.empty();
		}
	}

	static class EmailSenderStub implements EmailSender {
		@Override
		public void send(Email email, String subject, String body) {
		}
	}

	// mock은 호출 여부 검증
	static class EmailSenderMock implements EmailSender {
		List<Email> tos = new ArrayList<>();

		public List<Email> getTos() {
			return tos;
		}

		@Override
		public void send(Email email, String subject, String body) {
			tos.add(email);
		}
	}
}
