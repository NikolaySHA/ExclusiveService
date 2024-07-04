package com.exclusiveService.repo;

import com.exclusiveService.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<Customer> findByUsernameOrEmail(String username, String email);
    
    Optional<User> findByEmail(String email);
}
