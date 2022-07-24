package com.mcserverarchive.archive.controller;

import com.google.gson.Gson;
import com.mcserverarchive.archive.config.exception.RestErrorCode;
import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.in.CreateResourceRequest;
import com.mcserverarchive.archive.dtos.out.ErrorDto;
import com.mcserverarchive.archive.dtos.out.ResourceDto;
import com.mcserverarchive.archive.dtos.out.SimpleResourceDto;
import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.model.Resource;
import com.mcserverarchive.archive.repositories.ResourceRepository;
import com.mcserverarchive.archive.repositories.UpdateRepository;
import com.mcserverarchive.archive.service.ResourceService;
import com.mcserverarchive.archive.service.ResourceUpdateService;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@RequestMapping("/archive")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ResourceUpdateService resourceUpdateService;
    private final ResourceRepository resourceRepository;
    private final UpdateRepository updateRepository;

    @GetMapping
    public Page<SimpleResourceDto> searchResources(@PageableDefault(size = 25, sort = "name", direction = Sort.Direction.DESC) Pageable pageable,
                                                   @QuerydslPredicate(root = Resource.class) Predicate predicate) throws RestException {
        if (pageable.getPageSize() > 50) throw new RestException(RestErrorCode.PAGE_SIZE_TOO_LARGE, "Page size is too large (%s > %s)", pageable.getPageSize(), 50);
        return this.resourceService.searchResources(pageable, predicate);
    }

    @PostMapping
    public void createResource(@RequestBody String map) throws RestException { // Could use Map<String, Object> instead of String
        CreateResourceRequest request = new Gson().fromJson(map, CreateResourceRequest.class);
        this.resourceService.createResource(request);
    }

    @PostMapping("/{resourceId}/edit")
    public void updateResourceInfo(Account account, @PathVariable int resourceId, @RequestParam(value = "file", required = false) MultipartFile file, CreateResourceRequest request) throws RestException {
        this.resourceService.updateResource(account, resourceId, file, request);
    }

    @GetMapping("/{resourceId}")
    public Object getResource(@PathVariable int resourceId) {
        Resource resource = this.resourceService.getResource(resourceId);
        if(resource == null) return ErrorDto.create(404, "This resource does not exist :(");

        int totalDownloads = this.updateRepository.getTotalDownloads(resource.getId()).orElse(0);

        return ResourceDto.create(resource, totalDownloads);
    }

    // TODO: properly delete
    @DeleteMapping("/{resourceId}/delete")
    public void deleteResource(@PathVariable int resourceId) {
        //this.resourceRepository.updateStatusById(resourceId, "removed");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST");
            }
        };
    }
}
