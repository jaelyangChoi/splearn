package tobyspring.splearn.domain;

public interface PasswordEncoder {
	public String encode(String password);

	public boolean matches(String password, String passwordHash);
}
