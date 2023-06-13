package org.eimerarchive.archive.controller;


import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.in.CreateUpdateRequest;
import org.eimerarchive.archive.service.ResourceUpdateService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
public class ResourceUpdateController {
    private final ResourceUpdateService resourceUpdateService;

    @PostMapping("/upload")
    public ResponseEntity<?> createUpdate(@RequestHeader("authorization") String token, @RequestParam("file") MultipartFile file, @RequestPart("data") String data) throws RestException {
        return this.resourceUpdateService.createUpdate(file, new Gson().fromJson(data, CreateUpdateRequest.class));
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<java.io.File> getFile(@PathVariable int updateId) {
        ResponseEntity<?> fileReturn = this.resourceUpdateService.getDownload(updateId);
        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.setContentDisposition(
                        ContentDisposition.attachment()
                                .filename(((ResourceUpdateService.FileReturn) fileReturn.getBody()).realName())
                                .build()
                ))
                .body(((ResourceUpdateService.FileReturn) fileReturn.getBody()).file());
    }
}
