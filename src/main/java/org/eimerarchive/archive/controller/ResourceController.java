package org.eimerarchive.archive.controller;

import lombok.RequiredArgsConstructor;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.in.resource.EditResourceRequest;
import org.eimerarchive.archive.dtos.out.ErrorResponse;
import org.eimerarchive.archive.dtos.out.ResourceResponse;
import org.eimerarchive.archive.dtos.out.SimpleResourceResponse;
import org.eimerarchive.archive.dtos.out.VersionsResponse;
import org.eimerarchive.archive.model.Resource;
import org.eimerarchive.archive.model.enums.ECategory;
import org.eimerarchive.archive.model.enums.EVersions;
import org.eimerarchive.archive.repositories.ResourceRepository;
import org.eimerarchive.archive.repositories.UpdateRepository;
import org.eimerarchive.archive.service.AccountService;
import org.eimerarchive.archive.service.ResourceService;
import org.eimerarchive.archive.service.ResourceUpdateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/archive")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final AccountService accountService;
    private final ResourceUpdateService resourceUpdateService;
    private final ResourceRepository resourceRepository;
    private final UpdateRepository updateRepository;

    @GetMapping("mods")
    public Page<SimpleResourceResponse> searchModResources(@PageableDefault(size = 25, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);
        return this.resourceService.searchResources(ECategory.MODS, pageable);
    }

    @GetMapping("plugins")
    public Page<SimpleResourceResponse> searchPluginResources(@PageableDefault(size = 25, sort = "name", direction = Sort.Direction.DESC) Pageable pageable, String category) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);
        return this.resourceService.searchResources(ECategory.PLUGINS, pageable);
    }

    @GetMapping("software")
    public Page<SimpleResourceResponse> searchSoftwareResources(@PageableDefault(size = 25, sort = "name", direction = Sort.Direction.DESC) Pageable pageable, String category) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);
        return this.resourceService.searchResources(ECategory.SOFTWARE, pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createResource(@RequestBody String map, @RequestHeader("authorization") String token) { // Could use Map<String, Object> instead of String
        return this.resourceService.createResource(map, token);
    }

    @PostMapping("/{resourceId}/edit")
    public ResponseEntity<?> updateResourceInfo(@RequestHeader("authorization") String token, @PathVariable int resourceId, @RequestBody EditResourceRequest request) throws RestException {

        if (!this.accountService.hasPermissionToUpload(token)) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.FORBIDDEN.getDescription()));
        }

        return this.resourceService.updateResource(resourceId, request);
    }

    @PostMapping("/slug/{slug}/edit")
    public ResponseEntity<?> updateResourceInfoSlug(@RequestHeader("authorization") String token, @PathVariable String slug, @RequestBody EditResourceRequest request) throws RestException {

        if (!this.accountService.hasPermissionToUpload(token)) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.FORBIDDEN.getDescription()));
        }

        return this.resourceService.updateResource(slug, request);
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<?> getResource(@PathVariable int resourceId) {
        Resource resource = this.resourceService.getResource(resourceId);
        if(resource == null) return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.RESOURCE_NOT_FOUND.getDescription()));

        int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);

        return ResponseEntity.ok(ResourceResponse.create(resource, totalDownloads));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getResourceBySlug(@PathVariable String slug) {
        Resource resource = this.resourceService.getResource(slug);
        if(resource == null) return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.RESOURCE_NOT_FOUND.getDescription()));

        int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);

        return ResponseEntity.ok(ResourceResponse.create(resource, totalDownloads));
    }

    // TODO: properly delete
    @DeleteMapping("/{resourceId}/delete")
    public void deleteResource(@PathVariable int resourceId) {
        //this.resourceRepository.updateStatusById(resourceId, "removed");
    }

    @GetMapping("versions")
    public VersionsResponse getVersions() {
        return VersionsResponse.create(Arrays.stream(EVersions.values()).map(e -> e.version).toList());
    }

    @GetMapping("categories")
    public ECategory[] getCategories() {
        return ECategory.values();
    }
}
