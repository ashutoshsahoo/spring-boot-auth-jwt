package in.ashu.practice.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

	private String token;

	private String type = "Bearer";

	private String username;

	private List<String> roles;

	public LoginResponse(String token, String username, List<String> roles) {
		super();
		this.token = token;
		this.username = username;
		this.roles = roles;
	}

}
