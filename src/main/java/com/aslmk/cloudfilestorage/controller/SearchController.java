package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.s3.StorageService;
import com.aslmk.cloudfilestorage.util.StorageInputValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    private final StorageService storageService;

    public SearchController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam(value = "query", required = false) String query,
                             Model model) {
        if (StorageInputValidator.isSearchQueryValid(query)) {

            if (query.replaceAll("/+$", "").isEmpty()) {
                model.addAttribute("searchResults", Collections.emptyList());
                return "search-page";
            }

            List<SearchResultsDto> searchResults = storageService.searchItem(query);
            model.addAttribute("searchResults", searchResults);
        }

        return "search-page";
    }
}
