package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.model.ECategory;
import org.eimerarchive.archive.model.File;
import org.eimerarchive.archive.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class ResourceResponse {

    private int id;
    private final int totalDownloads;
    private final String slug, name, description, blurb, source;
    private final ECategory category;
    private final byte[] logo;
    private final String account;

    private final List<UpdateResponse> updates;

    public static ResourceResponse create(Resource resource, int totalDownloads) {
        return new ResourceResponse(resource.getId(), totalDownloads, resource.getSlug(), resource.getName(), resource.getDescription(),
                resource.getBlurb(), resource.getSource(), resource.getCategory(), resource.getLogo(),
                "test", getFiles(resource.getFiles()));
    }

    private static List<UpdateResponse> getFiles(List<File> files) {
        List<UpdateResponse> updateResponses = new ArrayList<>();
        for(File file : files) {
            updateResponses.add(UpdateResponse.create(file));
        }
        return updateResponses;
    }
}
