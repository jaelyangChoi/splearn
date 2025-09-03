package tobyspring.splearn.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(
	@Email String email,
	@Size(min = 5, max = 20) String nickname,
	@Size(min = 8, max = 100) String password) {

	//추가 validation 로직 (or validator custom)
	// void validate(String param) {
	// }
}
