package com.mcserverarchive.archive.service;

import com.mcserverarchive.archive.config.custom.SiteConfig;
import com.mcserverarchive.archive.config.exception.RestErrorCode;
import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.in.CreateUpdateRequest;
import com.mcserverarchive.archive.dtos.in.resource.EditResourceUpdateRequest;
import com.mcserverarchive.archive.model.Account;
import com.mcserverarchive.archive.model.File;
import com.mcserverarchive.archive.model.Resource;
import com.mcserverarchive.archive.repositories.ResourceRepository;
import com.mcserverarchive.archive.repositories.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class ResourceUpdateService {
    private static final Path BASE_PATH = Path.of("./resources/plugins/");

    private final UpdateRepository updateRepository;
    private final ResourceRepository resourceRepository;
    private final SiteConfig siteConfig;

    @PostConstruct
    public void setup() {
        if (Files.notExists(BASE_PATH))
            BASE_PATH.toFile().mkdirs();
    }

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

    public void createUpdate(MultipartFile file, CreateUpdateRequest request) throws RestException {

        //if (this.updateRepository.getUpdatesCreateLastHour(account.getId()) > this.siteConfig.getMaxUpdatesPerHour()) throw new RestException(RestErrorCode.TOO_MANY_RESOURCE_UPDATES);

        if ((file.isEmpty() /*|| file.getSize() > this.siteConfig.getMaxUploadSize().toBytes()*/)) throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING, "Missing File");
        if ((!file.getOriginalFilename().endsWith(".jar") && !file.getOriginalFilename().endsWith(".zip"))) throw new RestException(RestErrorCode.WRONG_FILE_TYPE);
        if (request.isMissingRequirements()) throw new RestException(RestErrorCode.REQUIRED_ARGUMENTS_MISSING, "Missing name, version or description.");

        Resource resource = this.resourceRepository.findById(request.getId()).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_NOT_FOUND));
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
            connection.setRequestProperty("X-Custom-Auth-Key", "test");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
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
    }

    public FileReturn getDownload(int updateId) throws RestException {
        Path path = BASE_PATH.resolve(updateId + ".jar");
        if (!Files.exists(path)) throw new RestException(RestErrorCode.DOWNLOAD_NOT_FOUND, "File not found");
        File file = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        //if (file.getDownloadLink() != null) throw new RestException(RestErrorCode.WRONG_FILE_TYPE, "File is provided via an external URL");

        return new FileReturn(path.toFile(), file.getFilename());
    }

    public record FileReturn(java.io.File file, String realName) {}
}
