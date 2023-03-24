package org.eimerarchive.archive.dtos.in;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class CreateUpdateRequest {

    private int id;
    private String name;
    private String version;
    private String description;
    private List<Test[]> versions;
    private List<String> software;

    public boolean isMissingRequirements() {
        return this.version == null || this.version.isEmpty()
            || this.description == null || this.description.isEmpty();
    }
}