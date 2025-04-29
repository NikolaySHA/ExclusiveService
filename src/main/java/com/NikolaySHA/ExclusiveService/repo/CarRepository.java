package com.NikolaySHA.ExclusiveService.repo;

import com.NikolaySHA.ExclusiveService.model.entity.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByOwner_Email(String email);
    
    Page<Car> findAll(Pageable pageable);
    
    Optional<Car> findByLicensePlate(String licensePlate);
    
    @Query("SELECT c FROM Car c WHERE (:licensePlate IS NULL OR c.licensePlate LIKE %:licensePlate%) AND (:make IS NULL OR c.make LIKE %:make%) AND (:vin IS NULL OR c.vin LIKE %:vin%) AND (:customer IS NULL OR c.owner.name LIKE %:customer%)")
    Page<Car> searchCarsByFilter(@Param("licensePlate") String licensePlate, @Param("make") String make, @Param("vin") String vin, @Param("customer") String customer, Pageable pageable);
    
    @Query("SELECT c FROM Car c WHERE (:licensePlate IS NULL OR c.licensePlate LIKE %:licensePlate%) AND (:make IS NULL OR c.make LIKE %:make%) AND (:customer IS NULL OR c.owner.name LIKE %:customer%)")
    Page<Car> searchCarsWithoutVin(@Param("licensePlate") String licensePlate, @Param("make") String make, @Param("customer") String customer, Pageable pageable);
    
    Optional<Car> findByVin(String vin);
}
