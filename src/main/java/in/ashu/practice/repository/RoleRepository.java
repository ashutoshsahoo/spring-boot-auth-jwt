package in.ashu.practice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.ashu.practice.model.Role;
import in.ashu.practice.model.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(RoleType name);
}
