package com.NikolaySHA.ExclusiveService.web.controller;

import com.NikolaySHA.ExclusiveService.model.entity.Image;
import com.NikolaySHA.ExclusiveService.service.GalleryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
        // Код за качване на снимка
        
        return "redirect:/gallery"; // Пренасочване към страницата с галерията
    }
    
    @GetMapping
    public String getGallery(Model model) {
        List<Image> images = service.getAllImages();
        model.addAttribute("images", images);
        return "home-gallery"; // Това ще зареди HTML страницата с изображения
    }
}
