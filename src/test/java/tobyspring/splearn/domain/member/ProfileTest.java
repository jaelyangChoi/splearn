package tobyspring.splearn.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProfileTest {

	@Test
	void profile() {
		new Profile("jaeryang");
		new Profile("jaeryang123");
		new Profile("1234");
		new Profile("");
	}

	@Test
	void profileFail() {
		assertThatThrownBy(() -> new Profile("toolongtoolongtoolong")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Profile("AAA")).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> new Profile("프로필")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void url() {
		var profile = new Profile("jaeryang");

		assertThat(profile.url()).isEqualTo("@jaeryang");
	}
}
