package com.exclusiveService.service;

import com.exclusiveService.model.entity.Car;
import com.exclusiveService.model.dto.AddCarDataDTO;
import com.exclusiveService.repo.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    private final UserService userService;
    private final CarRepository carRepository;
    
    
    
    public CarService(UserService userService, CarRepository carRepository) {
        this.userService = userService;
        this.carRepository = carRepository;
    }
    
    public boolean addCar(AddCarDataDTO data) {
        Optional<Car> optionalCar = carRepository.findByLicensePlate(data.getLicensePlate());
        if (optionalCar.isPresent()){
            return false;
        }
        Car car = new Car();
        car.setMake(data.getMake());
        car.setModel(data.getModel());
        car.setLicensePlate(data.getLicensePlate());
        car.setColor(data.getColor());
        car.setVin(data.getVin());
        car.setOwner(userService.findLoggedUser());
        carRepository.save(car);
        return true;
        
    }
    public List<Car> findCarsByUser(String email) {
        return this.carRepository.findByOwner_Email(email);
    }
}
