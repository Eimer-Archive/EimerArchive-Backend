package org.eimerarchive.archive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eimerarchive.archive.dtos.in.Test;
import org.eimerarchive.archive.model.enums.EVersions;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "files")
@NoArgsConstructor
public class File {

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

    public File(String description, String originalFilename, String version, String name, List<Test[]> versions, List<String> software, Resource resource) {
        this.downloads = 0;
        this.description = description;
        this.filename = originalFilename;
        this.version = version;
        this.name = name;
        this.software = software;
        this.resource = resource;

        List<EVersions> versionsList = new ArrayList<>();
        for (Test[] test : versions) {
            for (Test t : test) {
                versionsList.add(EVersions.fromString(t.name));
            }
        }

        this.versions = versionsList;
    }
}
