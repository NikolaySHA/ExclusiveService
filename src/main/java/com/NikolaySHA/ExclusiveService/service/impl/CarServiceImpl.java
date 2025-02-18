package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarDataDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.repo.CarRepository;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    
    private final UserService userService;
    private final CarRepository carRepository;
    
    
    @Override
    public boolean addCar(CarDataDTO data) {
        Optional<Car> optionalCar = carRepository.findByLicensePlate(data.getLicensePlate());
        if (optionalCar.isPresent()){
            return false;
        }
        Car car = new Car();
        car.setMake(data.getMake());
        car.setModel(data.getModel());
        car.setLicensePlate(data.getLicensePlate());
        car.setColor(data.getColor());
        String vin = data.getVin();
        if (vin.isEmpty()) {
            vin = null;
        }
        car.setVin(vin);
        car.setOwner(userService.findLoggedUser());
        carRepository.save(car);
        return true;
    }
    
    @Override
    public List<Car> findCarsByUser(Long id) {
        return this.carRepository.findByOwner_Email(userService.findById(id).get().getEmail());
    }
    @Override
    public Optional<Car> findById(Long id) {
        return this.carRepository.findById(id);
    }
    
    
    @Override
    public void delete(Car car) {
        carRepository.delete(car);
    }
    
    @Override
    public Page<Car> findAllCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }
    
    @Override
    public Page<Car> searchCars(String licensePlate, String make, String vin, String customer, Pageable pageable) {
        Page<Car> cars;
        if (vin == null || vin.isEmpty()) {
            cars = carRepository.searchCarsWithoutVin(
                    licensePlate, make, customer, pageable);
        } else {
            cars = carRepository.searchCarsByFilter(
                    licensePlate, make, vin, customer, pageable);
        }
        
        return cars;
    }
    
    @Override
    public void updateCar(Long id, CarDataDTO car) {
        Optional<Car> toEdit = carRepository.findById(id);
        if (toEdit.isEmpty()){
            return;
        }
        Car editedCar = toEdit.get();
        editedCar.setLicensePlate(car.getLicensePlate());
        editedCar.setMake(car.getMake());
        editedCar.setModel(car.getModel());
        editedCar.setVin(car.getVin());
        editedCar.setColor(car.getColor());
        this.carRepository.save(editedCar);
    }
    
    @Override
    public boolean findByLicensePlate(String licensePlate) {
        return this.carRepository.findByLicensePlate(licensePlate).isPresent();
    }
    
    @Override
    public boolean existByVin(String vin) {
        if (vin.isEmpty()){
            return false;
        }
        return this.carRepository.findByVin(vin).isPresent();
    }
    
    @Override
    public Optional<Car> findByVin(String vin) {
        return carRepository.findByVin(vin);
    }
    
    
}
