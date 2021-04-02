package com.project.ai.model.service;

import com.project.ai.model.entity.Image;
import com.project.ai.model.entity.Person;
import com.project.ai.model.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image save(Image image) {
        return this.imageRepository.save(image);
    }

    public Image findById(long id) {
        if(this.imageRepository.existsById(id)){
            return this.imageRepository.getOne(id);
        }
        return null;
    }

    public void delete(Image image) {
        this.imageRepository.delete(image);
    }

    public List<Image> findAll() {
        return this.imageRepository.findAll();
    }
}
