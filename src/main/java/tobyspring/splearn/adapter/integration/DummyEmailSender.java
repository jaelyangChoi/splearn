package tobyspring.splearn.adapter.integration;

import org.springframework.context.annotation.Fallback;
import org.springframework.stereotype.Component;

import tobyspring.splearn.application.member.required.EmailSender;
import tobyspring.splearn.domain.shared.Email;

@Component
@Fallback //다른 구현체가 있다면 무시해줘
public class DummyEmailSender implements EmailSender {
	@Override
	public void send(Email email, String subject, String body) {
		System.out.println("DummyEmailSender send email " + email);
	}
}
