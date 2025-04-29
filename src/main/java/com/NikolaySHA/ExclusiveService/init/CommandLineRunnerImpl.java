package com.NikolaySHA.ExclusiveService.init;

import com.NikolaySHA.ExclusiveService.model.entity.UserRole;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import com.NikolaySHA.ExclusiveService.repo.RoleRepository;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final AppointmentService appointmentService;
    
    public void run(String... args) throws Exception {
        long count = this.roleRepository.count();
        if (count <= 0L) {
            List<UserRole> toInsert = (List)Arrays.stream(UserRolesEnum.values()).map((role) -> {
                return new UserRole(role);
            }).collect(Collectors.toList());
            this.roleRepository.saveAll(toInsert);
        }
    }
    
    public CommandLineRunnerImpl(final RoleRepository roleRepository, final AppointmentService appointmentService) {
        this.roleRepository = roleRepository;
        this.appointmentService = appointmentService;
    }
}
