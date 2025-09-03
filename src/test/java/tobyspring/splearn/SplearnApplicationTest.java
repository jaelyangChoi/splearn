package tobyspring.splearn;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class SplearnApplicationTest {

	/** 주의점
	 * MockedStatic<SpringApplication>을 닫지 않아(close 누락) 전역(static) 모킹이 다음 테스트까지 누수된다.
	 * 그 결과, 다른 테스트들이 애플리케이션 컨텍스트를 띄우려고 SpringApplication.run(...)을 호출해도 모킹된 동작이 가로채면서 컨텍스트 로딩이 깨지고, 질문의 에러(DefaultCacheAwareContextLoaderDelegate의 IllegalStateException → ParameterResolutionException)가 연쇄적으로 발생.
	 * 즉, static mock의 스코프를 테스트 메서드 내부로 확실히 제한해야 한다.
	 */
	@Test
	void run() {
		// 실제로 SpringApplication을 실행하면 안된다. mock을 쓰자.
		// try-with-resources 로 모킹 스코프를 이 블록으로 제한(테스트 끝나면 자동 close)
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {

			SplearnApplication.main(new String[0]);

			mocked.verify(() ->
				SpringApplication.run(Mockito.eq(SplearnApplication.class), Mockito.any(String[].class))
			);
		} // 여기서 mock이 자동으로 해제되어 다른 테스트에 영향 X

	}
}
