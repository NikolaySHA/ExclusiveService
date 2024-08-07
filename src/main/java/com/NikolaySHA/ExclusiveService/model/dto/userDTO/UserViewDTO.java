package com.NikolaySHA.ExclusiveService.model.dto.userDTO;

import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class UserViewDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private List<Car> cars;
    private List<Appointment> appointments;
    private List<UserRole> roles;
    
    public UserViewDTO() {
        this.cars = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.roles = new ArrayList<>();
    }
}