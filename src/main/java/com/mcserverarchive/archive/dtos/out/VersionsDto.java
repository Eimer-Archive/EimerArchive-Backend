package com.mcserverarchive.archive.dtos.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VersionsDto {
    private List<String> versions;

    public static VersionsDto create(List<String>versions) {
        return new VersionsDto(versions);
    }
}
