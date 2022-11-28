package com.mcserverarchive.archive.dtos.in.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditResourceRequest {
    private String name;
    private String blurb;
    private String source;
    private String description;
}
