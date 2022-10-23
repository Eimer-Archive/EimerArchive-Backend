package com.mcserverarchive.archive.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File {

    public File(String description, String filename, String version, String name, List<String> versions, List<String> software, Resource resource) {
        this.downloads = 0;
        this.description = description;
        this.filename = filename;
        this.version = version;
        this.name = name;
        this.software = software;
        this.resource = resource;

        List<EVersions> versionsList = new ArrayList<>();
        for (String s : versions) {
            versionsList.add(EVersions.valueOf(s.toUpperCase()));
        }
        this.versions = versionsList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String filename;

    // Minecraft Versions
    @ElementCollection
    private List<EVersions> versions;

    // File version/build number
    @Column(nullable = false)
    private String version;

    @ElementCollection
    private List<String> software;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int downloads;

    public File() {

    }
}
