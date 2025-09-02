package tobyspring.splearn.application.required;

import static org.assertj.core.api.Assertions.*;
import static tobyspring.splearn.domain.MemberFixture.*;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import tobyspring.splearn.domain.Member;

@DataJpaTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void createMember() {
		Member member = Member.register(createMemberRegisterRequest(), createPasswordEncoder());

		assertThat(member.getId()).isNull();

		memberRepository.save(member);

		assertThat(member.getId()).isNotNull();

		entityManager.flush();
	}

	@Test
	void duplicateEmailFail() {
		Member member = Member.register(createMemberRegisterRequest(), createPasswordEncoder());
		memberRepository.save(member);

		Member member2 = Member.register(createMemberRegisterRequest(), createPasswordEncoder());

		assertThatThrownBy(() -> memberRepository.save(member2))
			.isInstanceOf(DataIntegrityViolationException.class);

	}

}
