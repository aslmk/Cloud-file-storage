package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.dto.file.RenameFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
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

    public HomeController(UserPathResolver userPathResolver,
                          DirectoryListingService directoryListingService) {
        this.userPathResolver = userPathResolver;
        this.directoryListingService = directoryListingService;
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

        return "home";
    }
}
