package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.entity.Image;
import com.NikolaySHA.ExclusiveService.service.GalleryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/gallery")
public class GalleryController {
    
    private final GalleryService service;
    
    public GalleryController(GalleryService service) {
        this.service = service;
    }
    @GetMapping("/")
    public String gallery(){
        return "home-gallery";
    }
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("image") MultipartFile file,
                              @RequestParam("title") String title) {
        try {
            // Генериране на уникално име за файла
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            
            // Директория за съхранение
            String uploadDir = "uploads";
            
            // Създаване на директорията, ако не съществува
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Съхранение на файла
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Създаване на нов запис за изображението
            Image image = new Image();
            image.setUrl("/uploads/" + fileName);
            image.setTitle(title);
            image.setData(file.getBytes());
            image.setContentType(file.getContentType());
            service.saveImage(image);
            
        } catch (IOException e) {
            e.printStackTrace();
            //TODO да се изпише съобщение
            return "error-contact-admin";
        }
        
        return "redirect:/gallery";
    }
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteImage(@PathVariable Long id, @RequestParam("_method") String method) {
        try {
            Image image = service.getImageById(id);
            if (image != null) {
                Path filePath = Paths.get("uploads").resolve(image.getUrl().substring(1)); // Премахва първия '/'
                Files.deleteIfExists(filePath);
                service.deleteImage(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO да се изпише грешката
            return "error-contact-admin";
        }
        return "redirect:/gallery";
    }
    @GetMapping
    public String getGallery(Model model) {
        List<Image> images = service.getAllImages();
        model.addAttribute("images", images);
        return "home-gallery";
    }
    
}
