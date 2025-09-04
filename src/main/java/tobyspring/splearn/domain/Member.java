package tobyspring.splearn.domain;

import static java.util.Objects.*;
import static org.springframework.util.Assert.*;

import jakarta.persistence.Entity;

import org.hibernate.annotations.NaturalId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {

	@NaturalId //비즈니스적으로 의미있는 식별자
	private Email email;

	private String nickname;

	private String passwordHash;

	private MemberStatus status;

	// 정적 팩토리 메소드 -> new 클래스()를 안써서 이름을 통해 의도를 들어낼 수 있음.
	public static Member register(MemberRegisterRequest createRequest, PasswordEncoder passwordEncoder) {
		Member member = new Member();

		member.email = new Email(createRequest.email());
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
