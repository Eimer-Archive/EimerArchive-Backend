package com.mcserverarchive.archive.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table
@NoArgsConstructor
public class Token {

    public Token(LocalDateTime created, LocalDateTime expires, String ip, Account account) {
        this.created = created;
        this.expires = expires;
        this.ip = ip;
        this.account = account;
        token = java.util.UUID.randomUUID().toString();
    }

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private String token;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime expires;

    @Column
    private String ip;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
}