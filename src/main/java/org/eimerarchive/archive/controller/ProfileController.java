package org.eimerarchive.archive.controller;

import org.eimerarchive.archive.config.exception.RestException;
import org.eimerarchive.archive.dtos.out.ProfileDto;
import org.eimerarchive.archive.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ProfileDto getProfile(@PathVariable int id) throws RestException {
        return this.profileService.getProfileDto(id);
    }
}
