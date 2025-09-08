package tobyspring.splearn.domain.member;

import static java.util.Objects.*;
import static org.springframework.util.Assert.*;

import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.NaturalId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tobyspring.splearn.domain.AbstractEntity;
import tobyspring.splearn.domain.shared.Email;

@Entity
@Getter
@ToString(callSuper = true, exclude = "detail") //toString 한번 돌면 불필요한 select까지 일어날 수 있어서 제한.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractEntity {

	@NaturalId //비즈니스적으로 의미있는 식별자
	private Email email;

	private String nickname;

	private String passwordHash;

	private MemberStatus status;

	private MemberDetail detail;

	// 정적 팩토리 메소드 -> new 클래스()를 안써서 이름을 통해 의도를 들어낼 수 있음.
	public static Member register(MemberRegisterRequest createRequest, PasswordEncoder passwordEncoder) {
		Member member = new Member();

		member.email = new Email(createRequest.email());
		member.nickname = requireNonNull(createRequest.nickname());
		member.passwordHash = requireNonNull(passwordEncoder.encode(createRequest.password()));

		member.status = MemberStatus.PENDING;

		member.detail = MemberDetail.create();

		return member;
	}

	public void activate() {
		state(status == MemberStatus.PENDING, "PENDING 상태가 아닙니다.");

		this.status = MemberStatus.ACTIVE;
		this.detail.setActivatedAt();
	}

	public void deactivate() {
		state(status == MemberStatus.ACTIVE, "ACTIVE 상태가 아닙니다.");

		this.status = MemberStatus.DEACTIVATED;
		this.detail.deactivate();
	}

	public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(password, passwordHash);
	}

	public void updateInfo(MemberInfoUpdateRequest updateRequest) {
		state(status == MemberStatus.ACTIVE, "등록 완료 상태가 아니면 정보를 수정할 수 없습니다.");

		this.nickname = Objects.requireNonNull(updateRequest.nickname());

		this.detail.updateInfo(updateRequest);
	}

	public void changePassword(String password, PasswordEncoder passwordEncoder) {
		this.passwordHash = passwordEncoder.encode(requireNonNull(password));
	}

	public boolean isActive() {
		return status == MemberStatus.ACTIVE;
	}
}
