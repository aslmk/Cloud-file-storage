package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.UploadItemRequestDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.s3.StorageService;
import com.aslmk.cloudfilestorage.util.StorageInputValidator;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import com.aslmk.cloudfilestorage.util.UserSessionUtils;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class HomeController {
    private final UserSessionUtils userSessionUtils;
    private final StorageService storageService;
    private final UserPathResolver userPathResolver;
    private final StoragePathHelperUtil storagePathHelperUtil;

    public HomeController(UserSessionUtils userSessionUtils, StorageService storageService, UserPathResolver userPathResolver, StoragePathHelperUtil storagePathHelperUtil) {
        this.userSessionUtils = userSessionUtils;
        this.storageService = storageService;
        this.userPathResolver = userPathResolver;
        this.storagePathHelperUtil = storagePathHelperUtil;
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(value = "path", required = false) String path,
                           Model model,
                           HttpSession session) {
        UserEntity userEntity = userSessionUtils.getUserFromSession(session);

        String S3UserItemsPath = String.format("user-%s-files/", userEntity.getId());
        if (path != null && !path.isEmpty()) {
            S3UserItemsPath = UriComponentsBuilder.fromUriString(path)
                    .build()
                    .getPath();
        }
        List<S3ItemInfoDto> userItems = storageService.getAllItems(S3UserItemsPath);
        model.addAttribute("userItems", userItems);

        return "home";
    }


    @PostMapping("/upload")
    public String uploadItem(@RequestParam("items") MultipartFile[] items,
                             HttpSession session,
                             @RequestParam(value = "path", required = false) String path) throws BadRequestException {

        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path, userEntity.getId());

        UploadItemRequestDto uploadItemRequestDto = UploadItemRequestDto
                .builder()
                .multipartFiles(items)
                .parentPath(S3UserItemsPath)
                .build();

        storageService.saveItem(uploadItemRequestDto);

        return "redirect:/home?path="+ URLEncoder.encode(S3UserItemsPath, StandardCharsets.UTF_8);
    }
    @PostMapping("/remove")
    public String removeItem(@RequestParam("itemAbsolutePath") String itemAbsolutePath,
                             HttpSession session,
                             @RequestParam(value = "path", required = false) String path) {

        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path, userEntity.getId());
        storageService.removeItem(itemAbsolutePath);


        return "redirect:/home?path=" + URLEncoder.encode(S3UserItemsPath, StandardCharsets.UTF_8);
    }

    @PostMapping("/rename")
    public String renameItem(
            @RequestParam(value = "path", required = false) String path,
            @RequestParam("oldItemName") String oldItemName,
            @RequestParam("newItemName") String newItemName,
            HttpSession session) throws BadRequestException {
        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path, userEntity.getId());

        StorageInputValidator.validateItemName(newItemName);
        newItemName = storagePathHelperUtil.normalizeS3ObjectName(oldItemName, newItemName);

        storageService.renameItem(oldItemName, newItemName);
        return "redirect:/home?path=" + URLEncoder.encode(S3UserItemsPath, StandardCharsets.UTF_8);
    }

}
