package com.ExclusiveService.repo;

import com.ExclusiveService.model.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwner_Email(String email);
    
    Optional<Car> findByLicensePlate(String licensePlate);
}
