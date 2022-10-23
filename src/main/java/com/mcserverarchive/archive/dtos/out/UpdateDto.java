package com.mcserverarchive.archive.dtos.out;

import com.mcserverarchive.archive.model.EVersions;
import com.mcserverarchive.archive.model.File;

import java.util.List;

public record UpdateDto(int id, String description, String filename, List<EVersions> versions, List<String> software,
                        String name, String version, int downloads) {

    public static UpdateDto create(File file) {
        return new UpdateDto(file.getId(), file.getDescription(), file.getFilename(), file.getVersions(),
                file.getSoftware(), file.getName(), file.getVersion(), file.getDownloads());
    }
}
