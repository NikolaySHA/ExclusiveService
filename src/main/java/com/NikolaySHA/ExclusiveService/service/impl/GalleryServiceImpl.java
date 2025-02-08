package com.NikolaySHA.ExclusiveService.service.impl;

import com.NikolaySHA.ExclusiveService.model.entity.Image;
import com.NikolaySHA.ExclusiveService.repo.GalleryRepository;
import com.NikolaySHA.ExclusiveService.service.GalleryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryServiceImpl implements GalleryService {
    private final GalleryRepository repository;
    
    public GalleryServiceImpl(GalleryRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public List<Image> getAllImages() {
        return repository.findAll();
    }
    
    @Override
    public Image saveImage(Image image) {
        return repository.save(image);
    }
    
    @Override
    public void deleteImage(Long id) {
        try {
            repository.deleteById(id);
            System.out.println("Изображението с ID " + id + " е успешно изтрито.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Грешка при изтриването на изображението с ID " + id);
        }
    }
    
    
    @Override
    public Image getImageById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
}
