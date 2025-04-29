package com.NikolaySHA.ExclusiveService.repo;

import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r LEFT JOIN u.cars c WHERE (:name IS NULL OR u.name LIKE %:name%) AND (:email IS NULL OR u.email LIKE %:email%) AND (:role IS NULL OR r.role = :role) AND (COALESCE(:licensePlate, '') = '' OR c.licensePlate LIKE %:licensePlate%)")
    Page<User> searchUsers(@Param("name") String name, @Param("licensePlate") String licensePlate, @Param("email") String email, @Param("role") UserRolesEnum role, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN FETCH u.roles")
    Page<User> findAllWithRoles(Pageable pageable);
}