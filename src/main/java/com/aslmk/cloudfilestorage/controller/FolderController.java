package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;
import com.aslmk.cloudfilestorage.s3.FolderService;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;
    private final UserPathResolver userPathResolver;

    public FolderController(FolderService folderService, UserPathResolver userPathResolver) {
        this.folderService = folderService;
        this.userPathResolver = userPathResolver;
    }

    @GetMapping("/upload")
    public String uploadFolder(@ModelAttribute("uploadFolderRequest") UploadFolderRequestDto request,
                               @RequestParam(value = "path", required = false) String path) {

        folderService.saveFolder(request);
        return resolveRedirectUrl(path);
    }

    @PostMapping("/rename")
    public String renameFolder(@ModelAttribute("renameFolderRequest") RenameFolderRequestDto request,
                               @RequestParam(value = "path", required = false) String path) {

        request.setOldFolderName(request.getOldFolderName()+"/");
        request.setNewFolderName(request.getNewFolderName()+"/");

        String userRootFolder = userPathResolver.getUserRootFolder();
        request.setParentPath(userRootFolder + request.getParentPath());

        folderService.renameFolder(request);

        return resolveRedirectUrl(path);
    }

    @PostMapping("/remove")
    public String removeFolder(@RequestParam(value = "path", required = false) String path) {
        String folderFullPath = userPathResolver.getUserRootFolder() + path;

        folderService.removeFolder(folderFullPath);

        return resolveRedirectUrl(new S3Path(path).getParentPath());
    }

    private String resolveRedirectUrl(String path) {
        if (path == null) return "redirect:/home";

        String resolvedPath = userPathResolver.resolveUserS3Path(path);

        if (resolvedPath.replaceAll("/+$", "").trim().isEmpty()) return "redirect:/home";

        String encodedPath = userPathResolver.encodeUserS3Path(resolvedPath);
        return "redirect:/home?path=" + encodedPath;
    }
}
