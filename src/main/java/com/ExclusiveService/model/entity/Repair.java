package com.ExclusiveService.model.entity;

import com.ExclusiveService.model.enums.PaymentMethod;
import com.ExclusiveService.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "repairs")
public class Repair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private LocalDate date;
    @ManyToOne
    @NotNull
    private User user;
    
    @ManyToOne
    private Car car;
    
    @Column(nullable = false)
    private int paintDetails;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String comment;
    public Repair() {}
    
    public Repair(Appointment appointment) {
        this.date = appointment.getDate();
        this.user = appointment.getUser();
        this.car = appointment.getCar();
        this.paintDetails = appointment.getPaintDetails();
        this.comment = appointment.getComment();
        this.status = Status.IN_PROGRESS;
        // TODO: send a viber welcome message with viber REST api
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Car getCar() {
        return car;
    }
    
    public void setCar(Car car) {
        this.car = car;
    }
    
    public int getPaintDetails() {
        return paintDetails;
    }
    
    public void setPaintDetails(int paintDetails) {
        this.paintDetails = paintDetails;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}
