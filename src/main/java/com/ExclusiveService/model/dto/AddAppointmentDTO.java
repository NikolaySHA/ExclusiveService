package com.ExclusiveService.model.dto;

import com.ExclusiveService.model.entity.Car;
import com.ExclusiveService.model.entity.User;
import com.ExclusiveService.model.enums.PaymentMethod;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AddAppointmentDTO {
    @NotNull
    private LocalDate date;
    @NotNull
    private Car car;
    @NotNull
    private Integer paintDetails;
    @NotNull
    private PaymentMethod paymentMethod;
    @ManyToOne
    private User user;
    
    public AddAppointmentDTO() {
    }
    
    public User getCustomer() {
        return user;
    }
    
    public void setCustomer(User user) {
        this.user = user;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Car getCar() {
        return car;
    }
    
    public void setCar(Car car) {
        this.car = car;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public Integer getPaintDetails() {
        return paintDetails;
    }
    
    public void setPaintDetails(Integer paintDetails) {
        this.paintDetails = paintDetails;
    }
}
