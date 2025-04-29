package com.NikolaySHA.ExclusiveService.service;

import com.NikolaySHA.ExclusiveService.model.entity.Image;
import java.util.List;

public interface GalleryService {
    List<Image> getAllImages();
    
    Image saveImage(Image image);
    
    void deleteImage(Long id);
    
    Image getImageById(Long id);
}