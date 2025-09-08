package tobyspring.splearn.domain.member;

import static org.springframework.util.Assert.*;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tobyspring.splearn.domain.AbstractEntity;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends AbstractEntity {
	private Profile profile;

	private String introduction;

	@Column(nullable = false)
	private LocalDateTime registeredAt;

	private LocalDateTime activatedAt;

	private LocalDateTime deactivatedAt;

	//애그리거트 루트에서만 접근할 수 있도록 접근 제어 (package-private)
	static MemberDetail create() {
		MemberDetail memberDetail = new MemberDetail();
		memberDetail.registeredAt = LocalDateTime.now();

		return memberDetail;
	}

	void setActivatedAt() {
		isTrue(activatedAt == null, "이미 activatedAt이 설정되었습니다.");

		this.activatedAt = LocalDateTime.now();
	}

	void deactivate() {
		isTrue(deactivatedAt == null, "이미 deactivatedAt이 설정되었습니다.");

		this.deactivatedAt = LocalDateTime.now();
	}

	void updateInfo(MemberInfoUpdateRequest updateRequest) {
		this.profile = new Profile(updateRequest.profileAddress());
		this.introduction = Objects.requireNonNull(updateRequest.introduction());
	}
}
