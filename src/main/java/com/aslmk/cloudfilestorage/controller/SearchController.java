package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.s3.MinIoService;
import com.aslmk.cloudfilestorage.service.UserService;
import io.minio.errors.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class SearchController {
    private final MinIoService minIoService;
    private final UserService userService;

    public SearchController(MinIoService minIoService, UserService userService) {
        this.minIoService = minIoService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam(value = "query", required = false) String query,
                             Model model,
                             Principal principal) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = getUserFromPrincipal(principal);
        String S3UserItemsPath = String.format("user-%s-files/", userEntity.getId());
        if (query != null && !query.trim().isEmpty() && !query.equals("/")) {
            List<SearchResultsDto> searchResults = minIoService.searchItems(query, S3UserItemsPath);
            model.addAttribute("searchResults", searchResults);
        }

        return "search-page";
    }

    private UserEntity getUserFromPrincipal(Principal principal) {
        String currentUser = principal.getName();
        Optional<UserEntity> user = userService.findByUsername(currentUser);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }

        return user.get();
    }
}
