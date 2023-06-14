package org.eimerarchive.archive.service;

import com.google.gson.Gson;
import org.eimerarchive.archive.config.custom.SiteConfig;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.dtos.in.resource.CreateResourceRequest;
import org.eimerarchive.archive.dtos.in.resource.EditResourceRequest;
import org.eimerarchive.archive.dtos.out.ErrorResponse;
import org.eimerarchive.archive.dtos.out.resource.SimpleResourceResponse;
import org.eimerarchive.archive.model.enums.ECategory;
import org.eimerarchive.archive.model.Resource;
import org.eimerarchive.archive.repositories.ResourceRepository;
import org.eimerarchive.archive.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final UpdateRepository updateRepository;
    private final AccountService accountService;
    private final SiteConfig siteConfig;

    public Page<SimpleResourceResponse> searchResources(Pageable pageable) {
        return this.resourceRepository.findAll(pageable)
                .map(resource -> {
                    int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);
                    return SimpleResourceResponse.create(resource, totalDownloads);
                });
    }

    public Page<SimpleResourceResponse> searchResources(ECategory category, Pageable pageable) {
        return this.resourceRepository.findAllByCategory(category, pageable)
                .map(resource -> {
                    int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);
                    return SimpleResourceResponse.create(resource, totalDownloads);
                });
    }

    public Resource getResource(int resourceId) {
        Optional<Resource> resource = this.resourceRepository.findById(resourceId);

        if (resource.isEmpty()) return null;

        resource.get().setAuthor(null);
        return resource.get();
    }

    public Resource getResource(String slug) {
        Optional<Resource> resource = this.resourceRepository.findBySlugEqualsIgnoreCase(slug);

        if (resource.isEmpty()) return null;

        resource.get().setAuthor(null);
        return resource.get();
    }

    //TODO: More sanity checks
    public ResponseEntity<?> createResource(String map, String token) {
        if (!accountService.hasPermissionToUpload(token)) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.FORBIDDEN.getDescription()));
        }
        CreateResourceRequest request = new Gson().fromJson(map, CreateResourceRequest.class);

        if (this.resourceRepository.existsByNameEqualsIgnoreCase(request.getName())) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.RESOURCE_NAME_NOT_AVAILABLE.getDescription()));
        }
        if (request.getName().isEmpty() || request.getBlurb().isEmpty() || request.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.REQUIRED_ARGUMENTS_MISSING.getDescription()));
        }

        if (this.resourceRepository.existsBySlugEqualsIgnoreCase(request.getSlug())) {
            return ResponseEntity.badRequest().body(ErrorResponse.create(RestErrorCode.RESOURCE_SLUG_NOT_AVAILABLE.getDescription()));
        }

        Resource resource = new Resource(request.getName(), request.getSlug(), request.getDescription(),
                request.getBlurb(), request.getSource(),
                request.getAuthor(), ECategory.valueOf(request.getCategory().getName().toUpperCase()));

        this.resourceRepository.save(resource);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> updateResource(int resourceId, EditResourceRequest request) {

        resourceRepository.updateResource(resourceId, null, request.getName(), request.getSlug(), request.getBlurb(), request.getDescription(), request.getSource());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> updateResource(String slug, EditResourceRequest request) {

        resourceRepository.updateResourceBySlug(slug, null, request.getName(), request.getSlug(), request.getBlurb(), request.getDescription(), request.getSource());

        return ResponseEntity.ok().build();
    }
}