package com.mcserverarchive.archive.service;

import com.mcserverarchive.archive.config.custom.SiteConfig;
import com.mcserverarchive.archive.config.exception.RestErrorCode;
import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.in.CreateResourceRequest;
import com.mcserverarchive.archive.dtos.in.resource.EditResourceRequest;
import com.mcserverarchive.archive.dtos.in.resource.EditResourceUpdateRequest;
import com.mcserverarchive.archive.dtos.out.SimpleResourceDto;
import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.model.ECategory;
import com.mcserverarchive.archive.model.Resource;
import com.mcserverarchive.archive.repositories.ResourceRepository;
import com.mcserverarchive.archive.repositories.UpdateRepository;
import com.mcserverarchive.archive.util.ImageUtil;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final UpdateRepository updateRepository;
    private final SiteConfig siteConfig;

    public Page<SimpleResourceDto> searchResources(Pageable pageable, Predicate query) {
        return this.resourceRepository.findAll(query, pageable)
                .map(resource -> {
                    int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);
                    return SimpleResourceDto.create(resource, totalDownloads);
                });
    }

    public Page<SimpleResourceDto> searchResources(ECategory category, Pageable pageable, Predicate query) {
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
    public Resource createResource(CreateResourceRequest request) throws RestException {
        if (this.resourceRepository.existsByNameEqualsIgnoreCase(request.getName()))
            throw new RestException(RestErrorCode.RESOURCE_NAME_NOT_AVAILABLE);
        if (request.getName().isEmpty() || request.getBlurb().isEmpty() || request.getDescription().isEmpty())
            throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING);

        Resource resource = new Resource(request.getName(), request.getSlug(), request.getDescription(),
                request.getBlurb(), request.getSource(),
                request.getAuthor(), ECategory.valueOf(request.getCategory().getName().toUpperCase()));

        return this.resourceRepository.save(resource);
    }

    public boolean updateResource(int resourceId, EditResourceRequest request) {

        resourceRepository.updateResource(resourceId, null, request.getName(), request.getBlurb(), request.getDescription(), request.getSource());

        return true;
    }

    public boolean updateResource(String slug, EditResourceRequest request) {

        resourceRepository.updateResourceBySlug(slug, null, request.getName(), request.getBlurb(), request.getDescription(), request.getSource());

        return true;
    }
}