package com.NikolaySHA.ExclusiveService.service.impl;


import com.NikolaySHA.ExclusiveService.model.dto.ProtocolDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.TransferProtocol;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.repo.AppointmentRepository;
import com.NikolaySHA.ExclusiveService.repo.ProtocolRepository;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.ProtocolService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProtocolServiceImpl implements ProtocolService {
    private final ProtocolRepository protocolRepository;
    private final ModelMapper modelMapper;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    
    @Override
    @Transactional
    public void createTransferProtocol(Appointment data) {
        
        TransferProtocol transferProtocol = new TransferProtocol();
        transferProtocol.setDate(data.getDate());
        transferProtocol.setLicensePlate(data.getCar().getLicensePlate());
        transferProtocol.setModel(data.getCar().getModel());
        transferProtocol.setMake(data.getCar().getMake());
        transferProtocol.setCustomerName(data.getCar().getOwner().getName());
        
        if (data.getStatus().equals(Status.COMPLETED)) {
            transferProtocol.setFinished(false);
        } else {
            transferProtocol.setFinished(true);
        }
        protocolRepository.save(transferProtocol);
        List<TransferProtocol> protocols = new ArrayList<>(data.getProtocols());
        protocols.add(transferProtocol);
        data.setProtocols(protocols);
        appointmentService.save(data);
    }
    @Override
    public ProtocolDTO getTransferProtocolById(Long id) {
        return protocolRepository.findById(id)
                .map(this::map).orElseThrow(() -> new IllegalArgumentException("Not found!"));
    }
    @Override
    public void generatePdf(Long id, HttpServletResponse response) throws IOException, DocumentException {
        TransferProtocol transferProtocol = protocolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found!"));
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=transfer_protocol.pdf");
        
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        
        String fontPath = "src/main/resources/fonts/ARIBLK.TTF"; // Заменете с реалния път до шрифта
        Font fontH1 = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18);
        Font font = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
        Font checkboxFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.NORMAL, BaseColor.BLACK);
        
        document.add(new Paragraph("                    Приемо-предавателен протокол", fontH1));
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Днес - " + transferProtocol.getDate().toString() + " в гр. София", font));
        document.add(Chunk.NEWLINE);
        if (!transferProtocol.isFinished()) {
            document.add(new Paragraph("между 'Ексклузив Сервиз' ООД и " + transferProtocol.getCustomerName(), font));
        }
        else {
            document.add(new Paragraph("между " + transferProtocol.getCustomerName() + " и 'Ексклузив Сервиз' ООД", font));
        }
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("се подписа настоящият протокол за л.а.:", font));
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("                    Регистрационен номер - " + transferProtocol.getLicensePlate(), font));
        document.add(new Paragraph("                    Марка - " + transferProtocol.getMake(), font));
        document.add(new Paragraph("                    Модел - " + transferProtocol.getModel(), font));
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        if (!transferProtocol.isFinished()) {
            document.add(new Paragraph("Собственика на горепосоченото МПС или неговият представител удостоверяват с подписа си че:", font));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("\u25A1 Ремонта е извършен качествено и автомобила е приет без възражения", font));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("\u25A1 Има следните възражения относно извършения ремонт:", font));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("              .................................................................................", font));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("              .................................................................................", font));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("              .................................................................................", font));
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Приел :" + "                                              Предал : ", font));
            document.add(new Paragraph("                 " + transferProtocol.getCustomerName() + "                                'Ексклузив Сервиз' ООД", font));
        } else {
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Приел :" + "                                              Предал : ", font));
            document.add(new Paragraph("              'Ексклузив Сервиз' ООД                       " + transferProtocol.getCustomerName(), font));
        }
        document.close();
    }
    
    @Override
    public void delete(Long id, Long appointmentId) {
        
        Optional<TransferProtocol> protocolOpt = protocolRepository.findById(id);
        if (protocolOpt.isPresent()) {
            TransferProtocol protocol = protocolOpt.get();
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = appointmentOpt.get();
               
                appointment.getProtocols().remove(protocol);
                appointmentRepository.save(appointment);
            }
            protocolRepository.delete(protocol);
        }
    }
    private ProtocolDTO map(TransferProtocol data){
        return this.modelMapper.map(data, ProtocolDTO.class);
    }
}
