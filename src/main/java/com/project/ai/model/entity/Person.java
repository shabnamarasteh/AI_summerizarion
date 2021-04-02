package com.project.ai.model.entity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "AI_PERSON")
public class Person implements Serializable {
    @Id
    @Column(name = "id", columnDefinition = "number")
    @SequenceGenerator(name = "AI_PERSON_SEQ", sequenceName = "AI_PERSON_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "AI_PERSON_SEQ")
    private Long personId;

    @Column(name = "first_name", columnDefinition = "nvarchar2(100)")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "nvarchar2(100)")
    private String lastName;

    @OneToMany(mappedBy = "person")
    private List<Image> images;

    public Person() {
    }

    public Person(Long personId, String firstName, String lastName, List<Image> images) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.images = images;
    }

    public Person(long id) {
        this.personId = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
