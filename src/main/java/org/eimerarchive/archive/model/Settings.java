package org.eimerarchive.archive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class Settings {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(mappedBy = "settings")
    private Account account;

    // TODO: make this able to be longer
    @Column(nullable = false)
    private String about;

    public Settings(String about, Account account) {
        this.about = about;
        this.account = account;
    }
}
