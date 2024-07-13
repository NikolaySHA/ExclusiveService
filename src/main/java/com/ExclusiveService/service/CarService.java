package com.ExclusiveService.service;

import com.ExclusiveService.model.entity.Car;
import com.ExclusiveService.model.dto.AddCarDataDTO;
import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.model.enums.UserRoles;
import com.ExclusiveService.repo.CarRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface CarService {
    
    boolean addCar(AddCarDataDTO data);
    List<Car> findCarsByUser();
    
    Optional<Car> findById(Long id);
    
    void delete(Car car);
    
    List<Car> findAllCars();
    
}
