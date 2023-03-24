package org.eimerarchive.archive.service;

import org.eimerarchive.archive.config.custom.SiteConfig;
import org.eimerarchive.archive.config.exception.RestErrorCode;
import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.in.CreateUpdateRequest;
import org.eimerarchive.archive.dtos.in.resource.EditResourceUpdateRequest;
import org.eimerarchive.archive.dtos.out.ErrorDto;
import org.eimerarchive.archive.model.Account;
import org.eimerarchive.archive.model.File;
import org.eimerarchive.archive.model.Resource;
import org.eimerarchive.archive.repositories.ResourceRepository;
import org.eimerarchive.archive.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceUpdateService {
    private final UpdateRepository updateRepository;
    private final ResourceRepository resourceRepository;
    private final SiteConfig siteConfig;

    public File changeStatus(Account account, int updateId, String status) throws RestException {
        File file = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        //if (file.getResource().getAuthor().getId() != account.getId()) throw new RestException(RestErrorCode.FORBIDDEN);
        //file.setStatus(status);
        return this.updateRepository.save(file);
    }

    public File editUpdate(Account account, int updateId, EditResourceUpdateRequest request) throws RestException {
        File file = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        //if (file.getResource().getAuthor().getId() != account.getId()) throw new RestException(RestErrorCode.FORBIDDEN);

        String name = request.getName();
        if (name != null && !name.isEmpty())
            file.setName(name);

        String version = request.getVersion();
        if (version != null && !version.isEmpty())
            file.setVersion(version);

        String description = request.getDescription();
        if (description != null && !description.isEmpty())
            file.setDescription(description);

        return this.updateRepository.save(file);
    }

    public ResponseEntity<?> createUpdate(MultipartFile file, CreateUpdateRequest request) throws RestException {

        //if (this.updateRepository.getUpdatesCreateLastHour(account.getId()) > this.siteConfig.getMaxUpdatesPerHour()) throw new RestException(RestErrorCode.TOO_MANY_RESOURCE_UPDATES);

        if ((file.isEmpty() /*|| file.getSize() > this.siteConfig.getMaxUploadSize().toBytes()*/)) return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.REQUIRED_ARGUMENTS_MISSING.getDescription()));
        if ((!file.getOriginalFilename().endsWith(".jar") && !file.getOriginalFilename().endsWith(".zip"))) return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.WRONG_FILE_TYPE.getDescription()));;
        if (request.isMissingRequirements()) return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.REQUIRED_ARGUMENTS_MISSING.getDescription()));;

        Optional<Resource> optionalResource = this.resourceRepository.findById(request.getId());
        if (optionalResource.isEmpty()) return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.RESOURCE_NOT_FOUND.getDescription()));
        Resource resource = optionalResource.get();

        File update;
        if (file.isEmpty()) {
            update = new File(request.getDescription(), null, request.getVersion(), request.getName(), request.getVersions(), request.getSoftware(), resource);
        } else {
            update = new File(request.getDescription(), file.getOriginalFilename(), request.getVersion(), file.getName(), request.getVersions(), request.getSoftware(), resource);
        }
        this.updateRepository.save(update);

        try (InputStream inputStream = file.getInputStream()) {

            // PUT request to upload file to server with headers
            HttpURLConnection connection = (HttpURLConnection) new URL("https://mc-archive.justdoom.workers.dev/" + update.getResource().getId() + "/" + update.getId() + "/" + file.getOriginalFilename()).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("X-Custom-Auth-Key", this.siteConfig.getKey());
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(100000);
            connection.setReadTimeout(100000);
            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            connection.getInputStream();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getDownload(int updateId) {
        Optional<File> update = updateRepository.findById(updateId);
        if (update.isEmpty()) {
            return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND.getDescription()));
        }

        // TODO: i think this is done weirdly, redo at some point
        String link = ""; //this.resourceUpdateService.getDownload(updateId);
        if (link == null) {
            return ResponseEntity.badRequest().body(ErrorDto.create(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND.getDescription()));
        }

        // TODO: restrict files that are private
//        Optional<Project> project = projectRepository.findById(id);
//        if (project.isEmpty()) {
//            return ResponseEntity.ok().body(APIUtil.createErrorResponse("Project not found"));
//        }

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(link)).build();
    }

    public record FileReturn(java.io.File file, String realName) {}
}
