package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.s3.MinIoService;
import com.aslmk.cloudfilestorage.util.UserSessionUtils;
import io.minio.errors.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
public class SearchController {
    private final MinIoService minIoService;
    private final UserSessionUtils userSessionUtils;

    public SearchController(MinIoService minIoService, UserSessionUtils userSessionUtils) {
        this.minIoService = minIoService;
        this.userSessionUtils = userSessionUtils;
    }

    @GetMapping("/search")
    public String searchItem(@RequestParam(value = "query", required = false) String query,
                             Model model,
                             HttpSession session) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = String.format("user-%s-files/", userEntity.getId());
        if (query != null && !query.trim().isEmpty() && !query.equals("/")) {
            List<SearchResultsDto> searchResults = minIoService.searchItems(query, S3UserItemsPath);
            model.addAttribute("searchResults", searchResults);
        }

        return "search-page";
    }
}
