package com.mcserverarchive.archive.config.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@Getter
@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@AllArgsConstructor
public class SiteConfig {

    private final String domain;
    private final String key;
    private final int maxUpdatesPerHour;
    private final int maxCreationsPerHour;

    @DataSizeUnit(DataUnit.BYTES)
    private final DataSize maxUploadSize;
}
