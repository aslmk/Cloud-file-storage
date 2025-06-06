package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.s3.StorageService;
import com.aslmk.cloudfilestorage.util.StorageInputValidator;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import com.aslmk.cloudfilestorage.util.UserSessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    private final UserSessionUtils userSessionUtils;
    private final StorageService storageService;
    private final UserPathResolver userPathResolver;

    public SearchController(UserSessionUtils userSessionUtils, StorageService storageService, UserPathResolver userPathResolver) {
        this.userSessionUtils = userSessionUtils;
        this.storageService = storageService;
        this.userPathResolver = userPathResolver;
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam(value = "query", required = false) String query,
                             Model model,
                             HttpSession session) {
        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = userPathResolver.getUserRootFolder(userEntity.getId());
        if (StorageInputValidator.isSearchQueryValid(query)) {
            List<SearchResultsDto> searchResults = storageService.searchItem(query, S3UserItemsPath);
            model.addAttribute("searchResults", searchResults);
        }

        return "search-page";
    }
}
