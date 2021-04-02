package com.project.ai.controller;

import com.project.ai.model.entity.Image;
import com.project.ai.model.entity.Person;
import com.project.ai.model.service.ImageService;
import com.project.ai.model.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping(value = "/v1/person/{id}/image")
public class ImageRestController {
    private ImageService imageService;
    private PersonService personService;

    @Autowired
    public ImageRestController(ImageService imageService, PersonService personService) {
        this.imageService = imageService;
        this.personService = personService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String uploadView(Model model, @PathVariable long id) {
        model.addAttribute("perosnId", id);
        Person person = this.personService.findById(id);
        List<Image> imageList = new ArrayList<>();
        if (person == null || person.getPersonId() == null || person.getPersonId() == 0) {
            model.addAttribute("error", id);
            model.addAttribute("imageList", imageList);
            return "uploadForm";
        } else {
            imageList = person.getImages();
            model.addAttribute("imageList", imageList);
            return "uploadForm";
        }
    }

    @RequestMapping(value = "/{imageId}/delete" ,method = RequestMethod.POST)
    public String delete(RedirectAttributes redirectAttributes, @PathVariable long id, @PathVariable long imageId) throws FileNotFoundException {
        Image image = this.imageService.findById(imageId);
        if (image == null) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/v1/person/" + id ;
        }
        String path = ResourceUtils.getFile("classpath:static/images").getAbsolutePath();

        try {
            Files.delete(Paths.get(path + File.separator + image.getAddress()));
            this.imageService.delete(image);
            return "redirect:/v1/person/" + id;
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/v1/person/" + id ;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file,
                         RedirectAttributes redirectAttributes, @PathVariable long id) throws IOException {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/v1/person/" + id ;
        }
        Person person = this.personService.findById(id);
        if (person == null || person.getPersonId() == null || person.getPersonId() == 0) {
            redirectAttributes.addFlashAttribute("message", "Person Not Found");
            return "redirect:/v1/person/" + id ;
        }
        try {
            // Get the file and save it somewhere
            String path = ResourceUtils.getFile("classpath:static/images").getAbsolutePath();
            byte[] bytes = file.getBytes();
            String name = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
            Files.write(Paths.get(path + File.separator + name), bytes);
            Image image = new Image();
            image.setAddress(name);
            image.setPerson(person);
            this.imageService.save(image);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/v1/person/" + id ;
    }
}