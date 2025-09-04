package tobyspring.splearn.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import tobyspring.splearn.application.provided.MemberFinder;
import tobyspring.splearn.application.provided.MemberRegister;
import tobyspring.splearn.application.required.EmailSender;
import tobyspring.splearn.application.required.MemberRepository;
import tobyspring.splearn.domain.DuplicateEmailException;
import tobyspring.splearn.domain.Email;
import tobyspring.splearn.domain.Member;
import tobyspring.splearn.domain.MemberRegisterRequest;
import tobyspring.splearn.domain.PasswordEncoder;

/**
 * 도메인 로직과 외부 세계와의 상호작용을 절차적으로 구현
 */
@Service
@Transactional
@Validated
@RequiredArgsConstructor
class MemberModifyService implements MemberRegister { //서비스가 커지면 port가 서비스 분리의 기준이 된다.
	private final MemberFinder memberFinder;
	private final MemberRepository memberRepository;
	private final EmailSender emailSender;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Member register(MemberRegisterRequest registerRequest) {
		// check
		checkDuplicateEmail(registerRequest);

		// domain model -> 주요 로직
		Member member = Member.register(registerRequest, passwordEncoder);

		// repository
		memberRepository.save(member);

		// post process
		sendWelcomeEmail(member);

		return member;
	}

	@Override
	public Member activate(Long memberId) {
		Member member = memberFinder.find(memberId);

		member.activate();

		return memberRepository.save(member); //JPA는 자동으로 변경 감지해서 반영해주지만, 우리는 JPA가 아니라 Spring data를 사용하는거다. + event publication + auditing
	}

	// 디테일한 내용은 한번 감싸자.
	private void sendWelcomeEmail(Member member) {
		emailSender.send(member.getEmail(), "등록을 완료해주세요", "아래 링크를 클릭해서 등록을 완료해주세요.");
	}

	// 복잡해서 한눈에 안들어온다.
	private void checkDuplicateEmail(MemberRegisterRequest registerRequest) {
		if (memberRepository.findByEmail(new Email(registerRequest.email())).isPresent()) {
			throw new DuplicateEmailException("이미 사용중인 이메일입니다: " + registerRequest.email());
		}
	}
}
