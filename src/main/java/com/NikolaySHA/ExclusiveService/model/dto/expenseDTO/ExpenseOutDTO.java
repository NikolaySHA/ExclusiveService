package com.NikolaySHA.ExclusiveService.model.dto.expenseDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpenseOutDTO {
    @NotNull(message = "{error_message_not_null}")
    private String name;
    @Positive(message = "{error_message_positive_price}")
    private double price;
    private long appointmentId;
    
}
