package com.mcserverarchive.archive.dtos.in.resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditResourceUpdateRequest {
    private String name;
    private String version;
    private String description;
}
