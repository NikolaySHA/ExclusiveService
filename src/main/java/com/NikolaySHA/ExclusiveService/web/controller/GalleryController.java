package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.entity.Image;
import com.NikolaySHA.ExclusiveService.service.GalleryService;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping({"/gallery"})
public class GalleryController {
    private final GalleryService service;
    
    public GalleryController(GalleryService service) {
        this.service = service;
    }
    
    @GetMapping({"/"})
    public String gallery() {
        return "home-gallery";
    }
    
    @PostMapping({"/upload"})
    public String uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("title") String title) {
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uploadDir = "/app/uploads";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath, new LinkOption[0])) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            Image image = new Image();
            image.setUrl("/uploads/" + fileName);
            image.setTitle(title);
            image.setContentType(file.getContentType());
            this.service.saveImage(image);
            return "redirect:/gallery";
        } catch (IOException var8) {
            var8.printStackTrace();
            return "error-contact-admin";
        }
    }
    
    @PostMapping({"/delete/{id}"})
    public String deleteImage(@PathVariable Long id) {
        try {
            Image image = this.service.getImageById(id);
            if (image != null) {
                Path filePath = Paths.get("/uploads").resolve(image.getUrl().substring(1));
                Files.deleteIfExists(filePath);
                this.service.deleteImage(id);
            }
            
            return "redirect:/gallery";
        } catch (IOException var4) {
            var4.printStackTrace();
            return "error-contact-admin";
        }
    }
    
    @GetMapping
    public String getGallery(Model model) {
        List<Image> images = this.service.getAllImages();
        model.addAttribute("images", images);
        return "home-gallery";
    }
}
