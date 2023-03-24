package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.model.EVersions;
import org.eimerarchive.archive.model.File;

import java.util.List;

public record UpdateDto(int id, String description, String filename, List<String> versions, List<String> software,
                        String name, String version, int downloads) {

    public static UpdateDto create(File file) {
        return new UpdateDto(file.getId(), file.getDescription(), file.getFilename(), EVersions.toStringArray(file.getVersions()),
                file.getSoftware(), file.getName(), file.getVersion(), file.getDownloads());
    }
}
