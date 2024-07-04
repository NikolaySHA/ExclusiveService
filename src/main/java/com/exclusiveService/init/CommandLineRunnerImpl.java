package com.exclusiveService.init;

import com.exclusiveService.model.entity.Role;
import com.exclusiveService.model.enums.UserRoles;
import com.exclusiveService.repo.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
  
    private final RoleRepository roleRepository;
    
    public CommandLineRunnerImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        long count = this.roleRepository.count();
        if (count > 0) {
            return;
        }
        List<Role> toInsert = Arrays.stream(UserRoles.values())
                .map(role -> new Role(role.name()))
                .collect(Collectors.toList());
        this.roleRepository.saveAll(toInsert);

    }
}
