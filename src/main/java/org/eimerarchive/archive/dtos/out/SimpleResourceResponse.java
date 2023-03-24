package org.eimerarchive.archive.dtos.out;

import org.eimerarchive.archive.model.File;
import org.eimerarchive.archive.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class SimpleResourceResponse {

    private int id;
    private final String slug, name, blurb;

    private final int totalDownloads;
    private final byte[] logo;

    private final List<UpdateResponse> updates;

    public static SimpleResourceResponse create(Resource resource, int totalDownloads) {
        return new SimpleResourceResponse(resource.getId(), resource.getSlug(), resource.getName(), resource.getBlurb(), totalDownloads, resource.getLogo(),
                getUpdates(resource.getFiles()));
    }

    private static List<UpdateResponse> getUpdates(List<File> files) {
        List<UpdateResponse> updateResponses = new ArrayList<>();
        for(File file : files) {
            updateResponses.add(UpdateResponse.create(file));
        }
        return updateResponses;
    }
}
