package se.claremont.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.claremont.backend.user.repository.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);
	
	void deleteByUsername(String username);
	
}