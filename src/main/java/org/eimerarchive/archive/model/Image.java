package org.eimerarchive.archive.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "images")
@NoArgsConstructor
public class Image implements Serializable {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Lob
    @Column(nullable = false)
    private byte[] bytes;

    public Image(byte[] image) {
        this.bytes = image;
        this.id = UUID.randomUUID();
    }
}
