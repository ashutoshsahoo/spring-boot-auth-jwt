package in.ashu.practice.util;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import in.ashu.practice.config.JwtConfigProperties;
import in.ashu.practice.model.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class that provides methods for handling jwt.
 *
 * @author ashutoshsahoo
 *
 */
@Component
@Slf4j
public class JwtUtils implements Serializable {

	private static final long serialVersionUID = -7320249530407047386L;

	private final JwtConfigProperties jwtConfigProperties;

	public JwtUtils(JwtConfigProperties jwtConfigProperties) {
		super();
		this.jwtConfigProperties = jwtConfigProperties;
	}

	/*
	 * Retrieve username from jwt token.
	 */
	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(jwtConfigProperties.getSecret().getBytes()).build()
				.parseClaimsJws(token).getBody().getSubject();
	}

	/*
	 * Retrieve expiration date from jwt token.
	 */

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/*
	 * Retrieving all the information from token using the secret key.
	 */

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(jwtConfigProperties.getSecret().getBytes()).build()
				.parseClaimsJws(token).getBody();
	}

	/*
	 * Create token.
	 */
	public String generateToken(Authentication authentication) {

		SecretKey jwtSecretKey = Keys.hmacShaKeyFor(jwtConfigProperties.getSecret().getBytes());
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject(userPrincipal.getUsername()).setIssuedAt(new Date())
				.setHeaderParam("type", jwtConfigProperties.getType()).setIssuer(jwtConfigProperties.getIssuer())
				.setAudience(jwtConfigProperties.getAudience())
				.setExpiration(new Date(System.currentTimeMillis() + jwtConfigProperties.getTokenValidity()))
				.signWith(jwtSecretKey, SignatureAlgorithm.HS512).compact();
	}

	/*
	 * Validate token.
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(jwtConfigProperties.getSecret().getBytes()).build()
					.parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

}
