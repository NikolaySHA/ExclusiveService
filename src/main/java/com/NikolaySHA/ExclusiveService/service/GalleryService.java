package com.NikolaySHA.ExclusiveService.service;

import com.NikolaySHA.ExclusiveService.model.entity.Image;

import java.util.List;

public interface GalleryService {
    
    public List<Image> getAllImages();
    
    public Image saveImage(Image image);
    
    public void deleteImage(Long id);
    
    Image getImageById(Long id);
}
