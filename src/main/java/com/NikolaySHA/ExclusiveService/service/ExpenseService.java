package com.NikolaySHA.ExclusiveService.service;

import com.NikolaySHA.ExclusiveService.model.dto.expenseDTO.ExpenseInDTO;
import com.NikolaySHA.ExclusiveService.model.dto.expenseDTO.ExpenseOutDTO;
import java.util.List;

public interface ExpenseService {
    void createExpense(ExpenseOutDTO expenseOutDTO);
    
    void deleteExpense(Long id);
    
    ExpenseInDTO getExpenseById(Long id);
    
    List<ExpenseInDTO> GetExpensesForAppointment(Long id);
    
    double getTotalExpensesForAppointment(Long appointmentId);
}