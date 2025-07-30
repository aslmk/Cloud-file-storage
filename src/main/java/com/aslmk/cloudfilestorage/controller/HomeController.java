package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.file.DownloadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.MoveFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.RenameFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.DownloadFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import com.aslmk.cloudfilestorage.util.BreadcrumbUtil;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final UserPathResolver userPathResolver;
    private final DirectoryListingService directoryListingService;
    private final BreadcrumbUtil breadcrumbUtil;

    public HomeController(UserPathResolver userPathResolver,
                          DirectoryListingService directoryListingService,
                          BreadcrumbUtil breadcrumbUtil) {
        this.userPathResolver = userPathResolver;
        this.directoryListingService = directoryListingService;
        this.breadcrumbUtil = breadcrumbUtil;
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(value = "path", required = false) String path,
                           Model model) {
        String S3UserItemsPath = userPathResolver.resolveUserS3Path(path);
        List<S3ItemInfoDto> userItems = directoryListingService.listItems(S3UserItemsPath);
        model.addAttribute("userItems", userItems);

        model.addAttribute("renameFileRequest", new RenameFileRequestDto());
        model.addAttribute("renameFolderRequest", new RenameFolderRequestDto());
        model.addAttribute("uploadFileRequest", new UploadFileRequestDto());
        model.addAttribute("uploadFolderRequest", new UploadFolderRequestDto());
        model.addAttribute("downloadFileRequest", new DownloadFileRequestDto());
        model.addAttribute("downloadFolderRequest", new DownloadFolderRequestDto());
        model.addAttribute("breadcrumbPaths", breadcrumbUtil.getBreadcrumb(path));
        model.addAttribute("moveFileRequest", new MoveFileRequestDto());
        model.addAttribute("availableFolders", directoryListingService.listFolders(S3UserItemsPath));

        return "home";
    }
}
