package tobyspring.splearn.application.required;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static tobyspring.splearn.domain.MemberFixture.*;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tobyspring.splearn.domain.Member;
import tobyspring.splearn.domain.MemberRegisterRequest;

@DataJpaTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void createMember() {
		Member member = Member.register(createMemberRegisterRequest(), createPasswordEncoder());

		memberRepository.save(member);

		assertThat(member.getId()).isNull();

		entityManager.flush();

		assertThat(member.getId()).isNotNull();
	}


}
