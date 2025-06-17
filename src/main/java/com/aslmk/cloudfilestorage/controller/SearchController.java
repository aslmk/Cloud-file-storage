package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.s3.StorageService;
import com.aslmk.cloudfilestorage.util.StorageInputValidator;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    private final StorageService storageService;
    private final UserPathResolver userPathResolver;

    public SearchController(StorageService storageService, UserPathResolver userPathResolver) {
        this.storageService = storageService;
        this.userPathResolver = userPathResolver;
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam(value = "query", required = false) String query,
                             Model model) {
        String S3UserItemsPath = userPathResolver.getUserRootFolder();
        if (StorageInputValidator.isSearchQueryValid(query)) {
            List<SearchResultsDto> searchResults = storageService.searchItem(query, S3UserItemsPath);
            model.addAttribute("searchResults", searchResults);
        }

        return "search-page";
    }
}
