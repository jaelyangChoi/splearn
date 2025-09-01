package tobyspring.splearn.domain;

import static java.util.Objects.*;
import static org.springframework.util.Assert.*;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Member {

	private String email;

	private String nickname;

	private String passwordHash;

	private MemberStatus status;

	private Member() {
	}

	// 정적 팩토리 메소드 -> new 클래스()를 안써서 이름을 통해 의도를 들어낼 수 있음.
	public static Member create(MemberCreateRequest createRequest, PasswordEncoder passwordEncoder) {
		Member member = new Member();

		member.email = requireNonNull(createRequest.email());
		member.nickname = requireNonNull(createRequest.nickname());
		member.passwordHash = requireNonNull(passwordEncoder.encode(createRequest.password()));

		member.status = MemberStatus.PENDING;

		return member;
	}

	public void activate() {
		state(status == MemberStatus.PENDING, "PENDING 상태가 아닙니다.");

		this.status = MemberStatus.ACTIVE;
	}

	public void deactivate() {
		state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다.");

		this.status = MemberStatus.DEACTIVATED;
	}

	public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(password, passwordHash);
	}

	public void changeNickname(String nickname) {
		this.nickname = requireNonNull(nickname);
	}

	public void changePassword(String password, PasswordEncoder passwordEncoder) {
		this.passwordHash = passwordEncoder.encode(requireNonNull(password));
	}

	public boolean isActive() {
		return status == MemberStatus.ACTIVE;
	}
}
