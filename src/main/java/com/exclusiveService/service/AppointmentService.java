package com.exclusiveService.service;

import com.exclusiveService.model.dto.AddAppointmentDTO;
import com.exclusiveService.model.entity.Appointment;
import com.exclusiveService.model.entity.User;
import com.exclusiveService.model.enums.Status;
import com.exclusiveService.repo.AppointmentRepository;
import com.exclusiveService.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final CarService carService;
    
    
    public AppointmentService(AppointmentRepository appointmentRepository, UserService userService, CarService carService) {
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        this.carService = carService;
    }
    
    public boolean create(AddAppointmentDTO data) {
        User user = userService.findLoggedUser();
       // Optional<Car> optionalCar = carRepository.findByLicensePlate(data.getCar().getLicensePlate());
        Appointment appointment = new Appointment();
        appointment.setPaymentMethod(data.getPaymentMethod());
        appointment.setDate(data.getDate());
     //   appointment.setCar(optionalCar.get());
        appointment.setUser(user);
        appointment.setStatus(Status.SCHEDULED);
        appointment.setPaintDetails(data.getPaintDetails());
        this.appointmentRepository.save(appointment);
        return true;
    }
    
    public List<Appointment> getAppointments(String email) {
       
        List<Appointment> appointments = new ArrayList<>();
//        List<Appointment> byCustomerEmail = this.appointmentRepository.findByCustomer_Email(email);
//        if (!byCustomerEmail.isEmpty()) {
//            appointments = byCustomerEmail;
//        }
        return appointments;
    }

//    public Map<CategoryName, List<Appointment>> getAllByCategory() {
//        Map<CategoryName, List<Appointment>> result = new HashMap<>();
//
//        List<Car> allCar = categoryRepository.findAll();
//        for (Car car : allCar) {
//            List<Appointment> allByCategory = recipeRepository.findAllByCategory(car);
//            result.put(car.getName(), allByCategory);
//        }
//        return result;
  //  }
//    @Transactional
//    public void addToFavourites(Long id, long recipeId) {
//        Optional<User> optionalUser = userRepository.findById(id);
//        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
//        if (optionalUser.isEmpty() || optionalRecipe.isEmpty()){
//            return;
//        }
//        optionalUser.get().addFavourite(optionalRecipe.get());
//
//        userRepository.save(optionalUser.get());
//    }
    
}
