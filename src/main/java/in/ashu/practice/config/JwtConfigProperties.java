package in.ashu.practice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt") 
@Setter
@Getter
@NoArgsConstructor
public class JwtConfigProperties {

	private String secret;

	private String issuer;

	private String type;

	private String audience;
	
	private long tokenValidity;
}
