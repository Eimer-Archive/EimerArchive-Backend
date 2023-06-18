package org.eimerarchive.archive.repositories;

import org.eimerarchive.archive.model.enums.ERole;
import org.eimerarchive.archive.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

    default Role findRole(ERole name) {
        Optional<Role> queried = this.findByName(name);
        if (queried.isEmpty()) {
            this.saveAndFlush(new Role(null, name));

            return findRole(name);
        }

        return queried.get();
    }
}