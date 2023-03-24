package org.eimerarchive.archive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity(name = "accounts")
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 3, max = 24)
    @Column(nullable = false, unique = true)
    private String username;

    @Size(min = 4, max = 120)
    @Column(nullable = false)
    private String password;

    @Size(min = 3, max = 120)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime joined;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "settings_id")
    private Settings settings;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "author")
    private List<File> files = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    public Account(String username, String email, String password, Image image) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.joined = LocalDateTime.now();
        this.image = image;
    }
}