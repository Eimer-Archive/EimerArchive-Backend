package org.eimerarchive.archive.service;

import org.eimerarchive.archive.config.custom.SiteConfig;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.dtos.in.CreateResourceRequest;
import org.eimerarchive.archive.dtos.in.resource.EditResourceRequest;
import org.eimerarchive.archive.dtos.out.ErrorDto;
import org.eimerarchive.archive.dtos.out.SimpleResourceDto;
import org.eimerarchive.archive.model.ECategory;
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
    private final SiteConfig siteConfig;

    public Page<SimpleResourceDto> searchResources(Pageable pageable) {
        return this.resourceRepository.findAll(pageable)
                .map(resource -> {
                    int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);
                    return SimpleResourceDto.create(resource, totalDownloads);
                });
    }

    public Page<SimpleResourceDto> searchResources(ECategory category, Pageable pageable) {
        return this.resourceRepository.findAllByCategory(category, pageable)
                .map(resource -> {
                    int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);
                    return SimpleResourceDto.create(resource, totalDownloads);
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
    public ResponseEntity<?> createResource(CreateResourceRequest request) {
        if (this.resourceRepository.existsByNameEqualsIgnoreCase(request.getName())) {
            return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.RESOURCE_NAME_NOT_AVAILABLE.getDescription()));
        }
        if (request.getName().isEmpty() || request.getBlurb().isEmpty() || request.getDescription().isEmpty()) {
            return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.REQUIRED_ARGUMENTS_MISSING.getDescription()));
        }

        if (this.resourceRepository.existsBySlugEqualsIgnoreCase(request.getSlug())) {
            return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.RESOURCE_SLUG_NOT_AVAILABLE.getDescription()));
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