package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.UploadItemRequestDto;
import com.aslmk.cloudfilestorage.s3.StorageService;
import com.aslmk.cloudfilestorage.util.StorageInputValidator;
import com.aslmk.cloudfilestorage.util.StoragePathHelperUtil;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
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
    private final StorageService storageService;
    private final UserPathResolver userPathResolver;
    private final StoragePathHelperUtil storagePathHelperUtil;

    public HomeController(StorageService storageService, UserPathResolver userPathResolver, StoragePathHelperUtil storagePathHelperUtil) {
        this.storageService = storageService;
        this.userPathResolver = userPathResolver;
        this.storagePathHelperUtil = storagePathHelperUtil;
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(value = "path", required = false) String path,
                           Model model) {
        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path);
        List<S3ItemInfoDto> userItems = storageService.getAllItems(S3UserItemsPath);
        model.addAttribute("userItems", userItems);

        return "home";
    }


    @PostMapping("/upload")
    public String uploadItem(@RequestParam("items") MultipartFile[] items,
                             @RequestParam(value = "path", required = false) String path) throws BadRequestException {

        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path);

        UploadItemRequestDto uploadItemRequestDto = UploadItemRequestDto
                .builder()
                .multipartFiles(items)
                .parentPath(S3UserItemsPath)
                .build();

        storageService.saveItem(uploadItemRequestDto);
        String encodedPath = userPathResolver.encodeUserS3Path(path);
        return "redirect:/home?path="+ encodedPath;
    }
    @PostMapping("/remove")
    public String removeItem(@RequestParam("itemAbsolutePath") String itemAbsolutePath,
                             @RequestParam(value = "path", required = false) String path) {
        String userRootFolder = userPathResolver.getUserRootFolder();
        String normalizedItemAbsolutePath = userRootFolder + itemAbsolutePath;

        storageService.removeItem(normalizedItemAbsolutePath);

        String encodedPath = userPathResolver.encodeUserS3Path(path);
        return "redirect:/home?path=" + encodedPath;
    }

    @PostMapping("/rename")
    public String renameItem(
            @RequestParam(value = "path", required = false) String path,
            @RequestParam("oldItemName") String oldItemName,
            @RequestParam("newItemName") String newItemName) throws BadRequestException {
        StorageInputValidator.validateItemName(newItemName);

        String userRootFolder = userPathResolver.getUserRootFolder();
        String normalizedOldItemAbsolutePath = userRootFolder + oldItemName;
        newItemName = storagePathHelperUtil.normalizeS3ObjectName(normalizedOldItemAbsolutePath, newItemName);

        storageService.renameItem(normalizedOldItemAbsolutePath, newItemName);

        String encodedPath = userPathResolver.encodeUserS3Path(path);
        return "redirect:/home?path=" + encodedPath;
    }

}
