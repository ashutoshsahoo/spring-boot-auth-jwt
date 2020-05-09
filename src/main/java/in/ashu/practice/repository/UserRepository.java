package in.ashu.practice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashu.practice.model.UserDao;

public interface UserRepository extends JpaRepository<UserDao, Long> {

	Optional<UserDao> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

}
