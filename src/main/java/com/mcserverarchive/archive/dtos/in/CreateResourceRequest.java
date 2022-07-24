package com.mcserverarchive.archive.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateResourceRequest {

    private String name;
    private String blurb;
    private String source;
    private String description;
    private String category;
    private String author;
}