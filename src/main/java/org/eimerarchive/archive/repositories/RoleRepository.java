package org.eimerarchive.archive.repositories;

import org.eimerarchive.archive.model.ERole;
import org.eimerarchive.archive.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}