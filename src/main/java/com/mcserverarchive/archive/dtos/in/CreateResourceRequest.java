package com.mcserverarchive.archive.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CreateResourceRequest {

    private String name;
    private String slug;
    private String blurb;
    private String source;
    private String description;
    private String author;
    private Category category;

    @Getter
    @Setter
    public class Category {
        private String name;
    }
}