package org.eimerarchive.archive.config.resolvers;

import org.eimerarchive.archive.model.Account;
import org.eimerarchive.archive.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@AllArgsConstructor
public class AccountArgumentResolver implements HandlerMethodArgumentResolver {
    private final AccountRepository accountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Account.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (SecurityContextHolder.getContext().getAuthentication() == null
                || !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account))
            return null;

        return accountRepository.getById(((Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }
}