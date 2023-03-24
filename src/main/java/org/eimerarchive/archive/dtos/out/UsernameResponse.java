package org.eimerarchive.archive.dtos.out;

import java.util.List;

public record UsernameResponse(String username, List<String> roles, long id) {

}
