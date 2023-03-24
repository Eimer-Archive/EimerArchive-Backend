package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.model.enums.EVersions;
import org.eimerarchive.archive.model.File;

import java.util.List;

public record UpdateResponse(int id, String description, String filename, List<String> versions, List<String> software,
                             String name, String version, int downloads) {

    public static UpdateResponse create(File file) {
        return new UpdateResponse(file.getId(), file.getDescription(), file.getFilename(), EVersions.toStringArray(file.getVersions()),
                file.getSoftware(), file.getName(), file.getVersion(), file.getDownloads());
    }
}
