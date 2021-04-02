package com.project.ai.controller;
import com.project.ai.model.entity.Image;
import com.project.ai.model.entity.Person;
import com.project.ai.model.service.ImageService;
import com.project.ai.model.service.PersonService;
import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping(value = "/v1/person")
public class PersonRestController {
    private PersonService personService;
    private ImageService imageService;
    @Autowired
    public PersonRestController(PersonService personService, ImageService imageService) {
        this.personService = personService;
        this.imageService = imageService;
    }

    @RequestMapping(value = "/check",method = RequestMethod.GET)
    public String checkimageForm(){
        return "checkForm";
    }

    @RequestMapping(value = "/check",method = RequestMethod.POST)
    public String checkimage(@RequestParam("file") MultipartFile file,Model model) throws IOException {
        model.addAttribute("imageFound", new Image());
        String path = ResourceUtils.getFile("classpath:static/imageUpload").getAbsolutePath();
        byte[] bytes = file.getBytes();
        String name = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType()).split("/")[1];
        Files.write(Paths.get(path + File.separator + name), bytes);

        final FImage image1 = ImageUtilities.readF(new File(path + File.separator + name));
        List<Image> imageList = this.imageService.findAll();
        for (Image image : imageList) {
            final FImage image2 = ImageUtilities.readF(new File(
                    ResourceUtils.getFile(
                            "classpath:static/images").getAbsolutePath()+ File.separator +
                            image.getAddress()));
            final HaarCascadeDetector detector = HaarCascadeDetector.BuiltInCascade.frontalface_alt2.load();
            final FKEFaceDetector kedetector = new FKEFaceDetector(detector);

            final FacePatchFeature.Extractor extractor = new FacePatchFeature.Extractor();
            final FaceFVComparator<FacePatchFeature, FloatFV> comparator =
                    new FaceFVComparator<>(FloatFVComparison.EUCLIDEAN);

            final FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage> engine =
                    new FaceSimilarityEngine<>(kedetector, extractor, comparator);

            // we need to tell the engine to use our images:
            engine.setQuery(image1, "image1");
            engine.setTest(image2, "image2");
            engine.performTest();

            System.out.println(engine.getSimilarityDictionary().entrySet().size());
            for (final Map.Entry<String, Map<String, Double>> e : engine.getSimilarityDictionary().entrySet()) {
                double bestScore = Double.MAX_VALUE;
                String best = null;
                for (final Map.Entry<String, Double> matches : e.getValue().entrySet()) {
                    if (matches.getValue() < bestScore) {
                        bestScore = matches.getValue();
                        best = matches.getKey();
                        System.out.println("----------best---"+best);
                        System.out.println("----------bestS---"+bestScore);
                    }
                }
                ////////////////////////////////////////////////////////////////
                if (bestScore>60.0)
                {
                    System.out.println("found!!!!");
                    System.out.println("----------found-------"+image.getAddress()+"**********"+image.getPerson().getFirstName());
                    model.addAttribute("imageFound", image);
                    return "person";
                }

                ////////////////////////////////////////////////////////////////
                // and this composites the original two images together, and draws
                // the matching pair of faces:
//                final FImage img = new FImage(image1.width + image2.width, Math.max(image1.height, image2.height));
//                img.drawImage(image1, 0, 0);
//                img.drawImage(image2, image1.width, 0);
//
//                img.drawShape(engine.getBoundingBoxes().get(e.getKey()), 1F);
//
//                final Rectangle r = engine.getBoundingBoxes().get(best);
//                r.translate(image1.width, 0);
//                img.drawShape(r, 1F);
//
//                // and finally displays the result
//                ImageUtilities.write(img,new File("output.jpg"));
                System.out.println("----------------117");
            }
        }
        return "person";
    }


    @RequestMapping(method = RequestMethod.GET)
    public String showAll(Model model) throws IOException {
        model.addAttribute("PersonList" , this.personService.findAll());
        return "allPerson";
    }

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String showRegister(Model model) throws IOException {
        model.addAttribute("Person" , new Person());
        return "register";
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public String personInfo(Model model, @PathVariable long id) throws IOException {
        model.addAttribute("Person" , this.personService.findById(id));
        return "register";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String register(@ModelAttribute Person person, Model model) throws IOException {
        person = this.personService.save(person);
        model.addAttribute("Person" , person);
        return "redirect:/v1/person/" + person.getPersonId() ;

    }
}
