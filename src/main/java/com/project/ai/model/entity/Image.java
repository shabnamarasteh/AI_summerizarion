package com.project.ai.model.entity;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "AI_IMAGE")
public class Image implements Serializable {
    @Id
    @Column(name = "ID", columnDefinition = "number")
    @SequenceGenerator(name = "AI_IMAGE_SEQ", sequenceName = "AI_IMAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "AI_IMAGE_SEQ")
    private Long imageId;

    @Column(name = "address", columnDefinition = "nvarchar2(200)")
    private String address;

    @ManyToOne
    @JoinColumn(name = "PERSON", foreignKey = @ForeignKey(name = "image_fk_person"))
    private Person person;

    public Image() {
    }

    public Image(Long imageId, String address, Person person) {
        this.imageId = imageId;
        this.address = address;
        this.person = person;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}