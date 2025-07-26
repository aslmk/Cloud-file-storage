package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.service.ItemSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class SearchController {
    private final ItemSearchService itemSearchService;

    public SearchController(ItemSearchService itemSearchService) {
        this.itemSearchService = itemSearchService;
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam(value = "query", required = false) String query,
                             Model model) {

        if (query == null) {
            return "search-page";
        }

        if (!query.replaceAll("/+$", "").trim().isEmpty()) {
            model.addAttribute("searchResults", Collections.emptyList());
            return "search-page";
        }

        List<SearchResultsDto> searchResults = itemSearchService.search(query);
        model.addAttribute("searchResults", searchResults);

        return "search-page";
    }
}
