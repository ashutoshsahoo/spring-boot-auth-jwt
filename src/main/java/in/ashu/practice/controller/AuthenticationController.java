package in.ashu.practice.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.ashu.practice.dto.LoginRequest;
import in.ashu.practice.dto.LoginResponse;
import in.ashu.practice.dto.SignupRequest;
import in.ashu.practice.model.UserDetailsImpl;
import in.ashu.practice.service.UserDetailsServiceImpl;
import in.ashu.practice.util.JwtUtils;

@RestController
@RequestMapping(path = "/auth", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	private final AuthenticationManager authManager;

	private final JwtUtils jwtUtils;

	private final UserDetailsServiceImpl userDetailsService;

	public AuthenticationController(AuthenticationManager authManager, JwtUtils jwtUtils,
			UserDetailsServiceImpl userDetailsService) {
		super();
		this.authManager = authManager;
		this.jwtUtils = jwtUtils;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/signin")
	public ResponseEntity<LoginResponse> createToken(@RequestBody @Valid LoginRequest request) {
		Authentication authentication = authManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final String token = jwtUtils.generateToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return ResponseEntity.ok(new LoginResponse(token, userDetails.getUsername(), roles));
	}

	@PostMapping(value = "/signup")
	public ResponseEntity<Void> saveUser(@RequestBody @Valid SignupRequest request) {
		userDetailsService.save(request);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
