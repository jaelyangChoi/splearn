package tobyspring.splearn.domain.member;

public class MemberFixture {
	public static MemberRegisterRequest createMemberRegisterRequest(String email) {
		return new MemberRegisterRequest(email, "jaeryang", "longsecret");
	}

	public static MemberRegisterRequest createMemberRegisterRequest() {
		return createMemberRegisterRequest("cjl2076@naver.com");
	}

	public static PasswordEncoder createPasswordEncoder() {
		return new PasswordEncoder() {
			@Override
			public String encode(String password) {
				return password.toUpperCase();
			}

			@Override
			public boolean matches(String password, String passwordHash) {
				return encode(password).equals(passwordHash);
			}
		};
	}
}
