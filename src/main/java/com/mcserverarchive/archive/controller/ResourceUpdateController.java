package com.mcserverarchive.archive.controller;


import com.google.gson.Gson;
import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.in.CreateUpdateRequest;
import com.mcserverarchive.archive.service.ResourceUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class ResourceUpdateController {
    private final ResourceUpdateService resourceUpdateService;

    @PostMapping("/upload")
    public void createUpdate(@RequestParam("file") MultipartFile file, @RequestPart("data") String data) throws RestException, IOException {
//        Path resourcePath = Path.of("C:\\Users\\prest\\Downloads\\txt.txt");
//        try (InputStream inputStream = file.getInputStream()) {
//            Files.copy(inputStream, resourcePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.resourceUpdateService.createUpdate(file, new Gson().fromJson(data, CreateUpdateRequest.class));
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<java.io.File> getFile(@PathVariable int updateId) throws RestException {
        ResourceUpdateService.FileReturn fileReturn = this.resourceUpdateService.getDownload(updateId);
        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.setContentDisposition(
                        ContentDisposition.attachment()
                                .filename(fileReturn.realName())
                                .build()
                ))
                .body(fileReturn.file());
    }
}
