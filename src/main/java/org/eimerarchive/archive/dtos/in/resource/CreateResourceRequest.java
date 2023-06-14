package org.eimerarchive.archive.dtos.in.resource;

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
    public static class Category {
        private String name;
    }
}