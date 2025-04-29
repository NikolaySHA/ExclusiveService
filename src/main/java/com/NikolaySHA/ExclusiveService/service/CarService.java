package com.NikolaySHA.ExclusiveService.service;

import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarDataDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    boolean addCar(CarDataDTO data);
    
    List<Car> findCarsByUser(Long id);
    
    Optional<Car> findById(Long id);
    
    void delete(Car car);
    
    Page<Car> findAllCars(Pageable pageable);
    
    Page<Car> searchCars(String licensePlate, String make, String vin, String customer, Pageable pageable);
    
    void updateCar(Long id, CarDataDTO car);
    
    boolean findByLicensePlate(String licensePlate);
    
    boolean existByVin(String vin);
    
    Optional<Car> findByVin(String vin);
}
