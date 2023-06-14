package org.eimerarchive.archive.dtos.out.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VersionsResponse {
    private List<String> versions;

    public static VersionsResponse create(List<String>versions) {
        return new VersionsResponse(versions);
    }
}
