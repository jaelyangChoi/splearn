package tobyspring.splearn.domain;

/**
 * 도메인에서 사용하므로, 애플리케이션의 required port에 두지말자.
 * 어댑터에서 애플리케이션으로, 애플리케이션에서 도메인으로 의존하는 것은 되지만 반대는 안된다.
 * 도메인도 애플리케이션 안에 들어있으므로, 애플리케이션 안에 required port를 정의한다는 헥사고날 아키텍처의 기본 구현 방식에 부합.
 */
public interface PasswordEncoder {
	public String encode(String password);

	public boolean matches(String password, String passwordHash);
}
