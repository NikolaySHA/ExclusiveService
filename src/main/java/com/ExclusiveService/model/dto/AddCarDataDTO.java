package com.ExclusiveService.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class AddCarDataDTO {
    @NotBlank(message = "Полето е задължително!")
    @Size(min = 5, max = 12, message = "Регистрационният номер трябва да съдържа между 5 и 12 символа!")
    private String licensePlate;
    @NotBlank
    @Size(min = 2, max = 30)
    private String make;
    @NotBlank
    @Size(min = 2, max = 50)
    private String model;
    @Pattern(regexp = "[A-Z0-9]{17}|", message = "Рамата не е задължително поле но трябва да съдържа 17 символа!")
    private String vin;
    private String color;
    
    public AddCarDataDTO() {
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getMake() {
        return make;
    }
    
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getVin() {
        return vin;
    }
    
    public void setVin(String vin) {
        this.vin = vin;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
}
