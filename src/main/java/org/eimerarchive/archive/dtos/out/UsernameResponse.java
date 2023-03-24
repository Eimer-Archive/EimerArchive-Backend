package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.model.Role;

import java.util.Set;

public record UsernameResponse(String username, Set<Role> role, long id) {

}
