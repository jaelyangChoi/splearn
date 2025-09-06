package tobyspring.splearn.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetail extends AbstractEntity{
	private String profile;

	private String introduction;

	private LocalDateTime registeredAt;

	private LocalDateTime activatedAt;

	private LocalDateTime deactivatedAt;

}
