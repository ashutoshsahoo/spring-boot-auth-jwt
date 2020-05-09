package in.ashu.practice.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.ashu.practice.dto.SignupRequest;
import in.ashu.practice.exception.EmailAlreadyExistsException;
import in.ashu.practice.exception.RoleDoesNotExistException;
import in.ashu.practice.exception.UsernameAlreadyExistsException;
import in.ashu.practice.model.Role;
import in.ashu.practice.model.RoleType;
import in.ashu.practice.model.UserDao;
import in.ashu.practice.model.UserDetailsImpl;
import in.ashu.practice.repository.RoleRepository;
import in.ashu.practice.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) {

		UserDao user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
		return UserDetailsImpl.build(user);
	}

	public UserDao save(SignupRequest request) {

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UsernameAlreadyExistsException(request.getUsername());
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException(request.getUsername());
		}

		Set<String> strRoles = request.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(RoleType.ROLE_USER).orElseThrow(RoleDoesNotExistException::new);
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
							.orElseThrow(() -> new RoleDoesNotExistException(role));
					roles.add(adminRole);
					break;
				case "mod":
					Role modRole = roleRepository.findByName(RoleType.ROLE_MODERATOR)
							.orElseThrow(() -> new RoleDoesNotExistException(role));
					roles.add(modRole);
					break;
				default:
					Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
							.orElseThrow(() -> new RoleDoesNotExistException(role));
					roles.add(userRole);
				}
			});
		}

		// Create new user's account
		UserDao user = new UserDao(request.getUsername(), request.getEmail(), encoder.encode(request.getPassword()),
				roles);
		return userRepository.save(user);
	}

}
