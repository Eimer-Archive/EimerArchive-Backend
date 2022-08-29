package com.mcserverarchive.archive.repositories;

import com.mcserverarchive.archive.model.ERole;
import com.mcserverarchive.archive.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}