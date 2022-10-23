package com.mcserverarchive.archive.dtos.out;

import com.mcserverarchive.archive.model.ECategory;
import com.mcserverarchive.archive.model.File;
import com.mcserverarchive.archive.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ResourceDto {

    private int id;
    private final int totalDownloads;
    private final String name;
    private final String description;
    private final String blurb;
    private final String source;
    private final ECategory category;
    private final byte[] logo;
    private final String account;

    private final List<UpdateDto> updates;

    public static ResourceDto create(Resource resource, int totalDownloads) {
        return new ResourceDto(resource.getId(), totalDownloads, resource.getName(), resource.getDescription(),
                resource.getBlurb(), resource.getSource(), resource.getCategory(), resource.getLogo(),
                "test", getFiles(resource.getFiles()));
    }

    private static List<UpdateDto> getFiles(List<File> files) {
        List<UpdateDto> updateDtos = new ArrayList<>();
        for(File file : files) {
            updateDtos.add(UpdateDto.create(file));
        }
        return updateDtos;
    }
}
