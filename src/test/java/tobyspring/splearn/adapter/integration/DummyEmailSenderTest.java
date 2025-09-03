package tobyspring.splearn.adapter.integration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import tobyspring.splearn.domain.Email;

class DummyEmailSenderTest {

	@Test
	@StdIo
	void dummyEmailSender(StdOut oUt) {
		DummyEmailSender dummyEmailSender = new DummyEmailSender();

		dummyEmailSender.send(new Email("cjl2076@naver.com"), "subject", "body");

		assertThat(oUt.capturedLines()[0]).isEqualTo("DummyEmailSender send email Email[address=cjl2076@naver.com]");
	}
}
