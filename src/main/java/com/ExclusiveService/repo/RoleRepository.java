package com.ExclusiveService.repo;

import com.ExclusiveService.model.entity.Role;
import com.ExclusiveService.model.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Role findByName(UserRoles userRoles);
}
