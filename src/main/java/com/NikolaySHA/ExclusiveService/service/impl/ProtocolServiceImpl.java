package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.dto.ProtocolDTO;
import com.NikolaySHA.ExclusiveService.model.entity.Appointment;
import com.NikolaySHA.ExclusiveService.model.entity.TransferProtocol;
import com.NikolaySHA.ExclusiveService.model.enums.Status;
import com.NikolaySHA.ExclusiveService.repo.AppointmentRepository;
import com.NikolaySHA.ExclusiveService.repo.ProtocolRepository;
import com.NikolaySHA.ExclusiveService.service.AppointmentService;
import com.NikolaySHA.ExclusiveService.service.ProtocolService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProtocolServiceImpl implements ProtocolService {
    private final ProtocolRepository protocolRepository;
    private final ModelMapper modelMapper;
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    
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
        
        this.protocolRepository.save(transferProtocol);
        List<TransferProtocol> protocols = new ArrayList(data.getProtocols());
        protocols.add(transferProtocol);
        data.setProtocols(protocols);
        this.appointmentService.save(data);
    }
    
    public ProtocolDTO getTransferProtocolById(Long id) {
        return (ProtocolDTO)this.protocolRepository.findById(id).map(this::map).orElseThrow(() -> {
            return new IllegalArgumentException("Not found!");
        });
    }
    
    public void generatePdf(Long id, HttpServletResponse response) throws IOException, DocumentException {
        TransferProtocol transferProtocol = (TransferProtocol)this.protocolRepository.findById(id).orElseThrow(() -> {
            return new IllegalArgumentException("Not found!");
        });
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=transfer_protocol.pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        String fontPath = "src/main/resources/fonts/ARIBLK.TTF";
        Font fontH1 = FontFactory.getFont(fontPath, "Identity-H", true, 18.0F);
        Font font = FontFactory.getFont(fontPath, "Identity-H", true, 12.0F);
        Font checkboxFont = FontFactory.getFont("Helvetica", 20.0F, 0, BaseColor.BLACK);
        document.add(new Paragraph("                    Приемо-предавателен протокол", fontH1));
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Днес - " + transferProtocol.getDate().toString() + " в гр. София", font));
        document.add(Chunk.NEWLINE);
        if (!transferProtocol.isFinished()) {
            document.add(new Paragraph("между 'Ексклузив Сервиз' ООД и " + transferProtocol.getCustomerName(), font));
        } else {
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
            document.add(new Paragraph("□ Ремонта е извършен качествено и автомобила е приет без възражения", font));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("□ Има следните възражения относно извършения ремонт:", font));
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
            document.add(new Paragraph("Приел :                                              Предал : ", font));
            document.add(new Paragraph("                 " + transferProtocol.getCustomerName() + "                                'Ексклузив Сервиз' ООД", font));
        } else {
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Приел :                                              Предал : ", font));
            document.add(new Paragraph("              'Ексклузив Сервиз' ООД                       " + transferProtocol.getCustomerName(), font));
        }
        
        document.close();
    }
    
    public void delete(Long id, Long appointmentId) {
        Optional<TransferProtocol> protocolOpt = this.protocolRepository.findById(id);
        if (protocolOpt.isPresent()) {
            TransferProtocol protocol = (TransferProtocol)protocolOpt.get();
            Optional<Appointment> appointmentOpt = this.appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                Appointment appointment = (Appointment)appointmentOpt.get();
                appointment.getProtocols().remove(protocol);
                this.appointmentRepository.save(appointment);
            }
            
            this.protocolRepository.delete(protocol);
        }
        
    }
    
    private ProtocolDTO map(TransferProtocol data) {
        return (ProtocolDTO)this.modelMapper.map(data, ProtocolDTO.class);
    }
    
    public ProtocolServiceImpl(final ProtocolRepository protocolRepository, final ModelMapper modelMapper, final AppointmentService appointmentService, final AppointmentRepository appointmentRepository) {
        this.protocolRepository = protocolRepository;
        this.modelMapper = modelMapper;
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
    }
}
