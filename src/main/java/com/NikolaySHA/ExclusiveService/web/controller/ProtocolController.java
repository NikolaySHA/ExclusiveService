package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.service.ProtocolService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping({"/protocol"})
@Controller
public class ProtocolController {
    private final ProtocolService protocolService;
    
    public ProtocolController(ProtocolService protocolService) {
        this.protocolService = protocolService;
    }
    
    @GetMapping({"/{id}"})
    public void getProtocolPdf(@PathVariable Long id, HttpServletResponse response) throws DocumentException, IOException {
        this.protocolService.generatePdf(id, response);
    }
    
    @PostMapping({"/delete/{appointmentId}/{id}"})
    public String deleteProtocol(@PathVariable("id") Long id, @PathVariable("appointmentId") Long appointmentId) {
        this.protocolService.delete(id, appointmentId);
        return "redirect:/appointments/" + appointmentId;
    }
}
