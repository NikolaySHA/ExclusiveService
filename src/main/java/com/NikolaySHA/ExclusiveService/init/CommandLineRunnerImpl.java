package com.NikolaySHA.ExclusiveService.init;

import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.UserRole;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.model.enums.UserRolesEnum;
import com.NikolaySHA.ExclusiveService.repo.RoleRepository;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.w3c.dom.UserDataHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
  
    private final RoleRepository roleRepository;
    private final AppointmentService appointmentService;
    @Override
    public void run(String... args) throws Exception {
        long count = this.roleRepository.count();
        if (count > 0) {
            return;
        }
        List<UserRole> toInsert = Arrays.stream(UserRolesEnum.values())
                .map(role -> new UserRole(role))
                .collect(Collectors.toList());
        this.roleRepository.saveAll(toInsert);

    }
}
