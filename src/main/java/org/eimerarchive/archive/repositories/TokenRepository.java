package org.eimerarchive.archive.repositories;

import org.eimerarchive.archive.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    Optional<Token> findByToken(UUID token);

    boolean existsByToken(UUID token);
}