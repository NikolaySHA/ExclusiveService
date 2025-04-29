package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.dto.expenseDTO.ExpenseInDTO;
import com.NikolaySHA.ExclusiveService.model.dto.expenseDTO.ExpenseOutDTO;
import com.NikolaySHA.ExclusiveService.service.ExpenseService;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final RestClient expense;
    
    public ExpenseServiceImpl(@Qualifier("expensesRestClient") RestClient expense) {
        this.expense = expense;
    }
    
    public void createExpense(ExpenseOutDTO expenseOutDTO) {
        ((RestClient.RequestBodySpec)this.expense.post().uri("/expenses", new Object[0])).body(expenseOutDTO).retrieve();
    }
    
    public void deleteExpense(Long id) {
        this.expense.delete().uri("/expenses/{id}", new Object[]{id}).retrieve().toBodilessEntity();
    }
    
    public ExpenseInDTO getExpenseById(Long id) {
        return (ExpenseInDTO)this.expense.get().uri("/expenses/{id}", new Object[]{id}).accept(new MediaType[]{MediaType.APPLICATION_JSON}).retrieve().body(ExpenseInDTO.class);
    }
    
    public List<ExpenseInDTO> GetExpensesForAppointment(Long id) {
        return (List)this.expense.get().uri("/expenses/appointment/{id}", new Object[]{id}).accept(new MediaType[]{MediaType.APPLICATION_JSON}).retrieve().body(new ParameterizedTypeReference<List<ExpenseInDTO>>() {
        });
    }
    
    public double getTotalExpensesForAppointment(Long appointmentId) {
        List<ExpenseInDTO> expenses = this.GetExpensesForAppointment(appointmentId);
        double total = 0.0;
        
        ExpenseInDTO expense;
        for(Iterator var5 = expenses.iterator(); var5.hasNext(); total += expense.getPrice()) {
            expense = (ExpenseInDTO)var5.next();
        }
        
        return total;
    }
}
