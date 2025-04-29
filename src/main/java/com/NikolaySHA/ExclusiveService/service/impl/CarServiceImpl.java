package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.dto.carDTO.CarDataDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Car;
import com.NikolaySHA.ExclusiveService.model.entity.User;
import com.NikolaySHA.ExclusiveService.repo.CarRepository;
import com.NikolaySHA.ExclusiveService.service.CarService;
import com.NikolaySHA.ExclusiveService.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {
    private final UserService userService;
    private final CarRepository carRepository;
    
    public boolean addCar(CarDataDTO data) {
        Optional<Car> optionalCar = this.carRepository.findByLicensePlate(data.getLicensePlate());
        if (optionalCar.isPresent()) {
            return false;
        } else {
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
            car.setOwner(this.userService.findLoggedUser());
            this.carRepository.save(car);
            return true;
        }
    }
    
    public List<Car> findCarsByUser(Long id) {
        return this.carRepository.findByOwner_Email(((User)this.userService.findById(id).get()).getEmail());
    }
    
    public Optional<Car> findById(Long id) {
        return this.carRepository.findById(id);
    }
    
    public void delete(Car car) {
        this.carRepository.delete(car);
    }
    
    public Page<Car> findAllCars(Pageable pageable) {
        return this.carRepository.findAll(pageable);
    }
    
    public Page<Car> searchCars(String licensePlate, String make, String vin, String customer, Pageable pageable) {
        Page cars;
        if (vin != null && !vin.isEmpty()) {
            cars = this.carRepository.searchCarsByFilter(licensePlate, make, vin, customer, pageable);
        } else {
            cars = this.carRepository.searchCarsWithoutVin(licensePlate, make, customer, pageable);
        }
        
        return cars;
    }
    
    public void updateCar(Long id, CarDataDTO car) {
        Optional<Car> toEdit = this.carRepository.findById(id);
        if (!toEdit.isEmpty()) {
            Car editedCar = (Car)toEdit.get();
            editedCar.setLicensePlate(car.getLicensePlate());
            editedCar.setMake(car.getMake());
            editedCar.setModel(car.getModel());
            editedCar.setVin(car.getVin());
            editedCar.setColor(car.getColor());
            this.carRepository.save(editedCar);
        }
    }
    
    public boolean findByLicensePlate(String licensePlate) {
        return this.carRepository.findByLicensePlate(licensePlate).isPresent();
    }
    
    public boolean existByVin(String vin) {
        return vin.isEmpty() ? false : this.carRepository.findByVin(vin).isPresent();
    }
    
    public Optional<Car> findByVin(String vin) {
        return this.carRepository.findByVin(vin);
    }
    
    public CarServiceImpl(final UserService userService, final CarRepository carRepository) {
        this.userService = userService;
        this.carRepository = carRepository;
    }
}
