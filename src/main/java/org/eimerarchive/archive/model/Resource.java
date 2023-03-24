package org.eimerarchive.archive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eimerarchive.archive.model.enums.ECategory;

import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String blurb;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private ECategory category;

    @Lob
    private byte[] logo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resource")
    private List<File> files;

    public Resource(String name, String slug, String description, String blurb, String source, String author, ECategory category) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.blurb = blurb;
        this.source = source;
        this.author = author;
        this.category = category;
    }
}