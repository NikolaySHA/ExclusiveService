//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.entity.Image;
import com.NikolaySHA.ExclusiveService.repo.GalleryRepository;
import com.NikolaySHA.ExclusiveService.service.GalleryService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GalleryServiceImpl implements GalleryService {
    private final GalleryRepository repository;
    
    public GalleryServiceImpl(GalleryRepository repository) {
        this.repository = repository;
    }
    
    public List<Image> getAllImages() {
        return this.repository.findAll();
    }
    
    public Image saveImage(Image image) {
        return (Image)this.repository.save(image);
    }
    
    public void deleteImage(Long id) {
        try {
            this.repository.deleteById(id);
            System.out.println("Изображението с ID " + id + " е успешно изтрито.");
        } catch (Exception var3) {
            var3.printStackTrace();
            System.out.println("Грешка при изтриването на изображението с ID " + id);
        }
        
    }
    
    public Image getImageById(Long id) {
        return this.repository.findById(id).orElse(null);
    }
}
