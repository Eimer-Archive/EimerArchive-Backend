package org.eimerarchive.archive.dtos.in.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditResourceRequest {
    private String name;
    private String slug;
    private String blurb;
    private String source;
    private String description;
}
