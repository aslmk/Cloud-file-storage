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
        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path, userEntity.getId());
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
        String encodedPath = userPathResolver.encodeUserS3Path(path, userEntity.getId());
        return "redirect:/home?path="+ encodedPath;
    }
    @PostMapping("/remove")
    public String removeItem(@RequestParam("itemAbsolutePath") String itemAbsolutePath,
                             HttpSession session,
                             @RequestParam(value = "path", required = false) String path) {
        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String userRootFolder = userPathResolver.getUserRootFolder(userEntity.getId());
        String normalizedItemAbsolutePath = userRootFolder + itemAbsolutePath;

        storageService.removeItem(normalizedItemAbsolutePath);

        String encodedPath = userPathResolver.encodeUserS3Path(path, userEntity.getId());
        return "redirect:/home?path=" + encodedPath;
    }

    @PostMapping("/rename")
    public String renameItem(
            @RequestParam(value = "path", required = false) String path,
            @RequestParam("oldItemName") String oldItemName,
            @RequestParam("newItemName") String newItemName,
            HttpSession session) throws BadRequestException {
        StorageInputValidator.validateItemName(newItemName);

        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String userRootFolder = userPathResolver.getUserRootFolder(userEntity.getId());
        String normalizedOldItemAbsolutePath = userRootFolder + oldItemName;
        newItemName = storagePathHelperUtil.normalizeS3ObjectName(normalizedOldItemAbsolutePath, newItemName);

        storageService.renameItem(normalizedOldItemAbsolutePath, newItemName);

        String encodedPath = userPathResolver.encodeUserS3Path(path, userEntity.getId());
        return "redirect:/home?path=" + encodedPath;
    }

}
