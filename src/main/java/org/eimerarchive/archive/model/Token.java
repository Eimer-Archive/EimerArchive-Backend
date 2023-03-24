package org.eimerarchive.archive.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table
@NoArgsConstructor
public class Token {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private String token;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime expires;

    @Column
    private long ip;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;

    public Token(long ip, Account account) {
        this.token = java.util.UUID.randomUUID().toString();
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusDays(7);
        this.ip = ip;
        this.account = account;
    }
}