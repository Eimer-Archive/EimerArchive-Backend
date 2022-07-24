package com.mcserverarchive.archive.config;

import com.mcserverarchive.archive.config.resovlers.AccountArgumentResolver;
import com.mcserverarchive.archive.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@AllArgsConstructor
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final AccountRepository accountRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AccountArgumentResolver(this.accountRepository));
    }
}