package com.exclusiveService.repo;

import com.exclusiveService.model.entity.Role;
import com.exclusiveService.model.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Role findByName(UserRoles userRoles);
}
