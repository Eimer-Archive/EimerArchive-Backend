package com.mcserverarchive.archive.controller;

import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.out.ProfileDto;
import com.mcserverarchive.archive.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ProfileDto getProfile(@PathVariable int id) throws RestException {
        return this.profileService.getProfileDto(id);
    }
}
