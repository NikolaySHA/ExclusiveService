package com.NikolaySHA.ExclusiveService.model.dto.expenseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExpenseInDTO {
    private Long id;
    private String name;
    private double price;
    private long appointmentId;
    
}
