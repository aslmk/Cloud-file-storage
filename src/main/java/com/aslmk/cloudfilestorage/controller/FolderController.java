package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.TargetFolderDto;
import com.aslmk.cloudfilestorage.dto.folder.DownloadFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.MoveFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.RenameFolderRequestDto;
import com.aslmk.cloudfilestorage.dto.folder.UploadFolderRequestDto;
import com.aslmk.cloudfilestorage.s3.FolderService;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;
    private final UserPathResolver userPathResolver;
    private final DirectoryListingService directoryListingService;

    public FolderController(FolderService folderService, UserPathResolver userPathResolver, DirectoryListingService directoryListingService) {
        this.folderService = folderService;
        this.userPathResolver = userPathResolver;
        this.directoryListingService = directoryListingService;
    }

    @PostMapping("/upload")
    public String uploadFolder(@ModelAttribute("uploadFolderRequest") UploadFolderRequestDto request,
                               @RequestParam(value = "path", required = false) String path) {
        request.setParentPath(userPathResolver.resolveUserS3Path(request.getParentPath()));
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

    @GetMapping("/download")
    public void downloadFolder(@ModelAttribute("downloadFolderRequest") DownloadFolderRequestDto request, HttpServletResponse response) throws IOException {
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + request.getFolderName() + ".zip" + "\"");

        request.setParentPath(userPathResolver.resolveUserS3Path(request.getParentPath()));
        request.setFolderName(request.getFolderName() + "/");
        folderService.downloadFolder(request, response.getOutputStream());
    }

    @PostMapping
    public String createFolder(@RequestParam(value = "path", required = false) String path,
                             @RequestParam(value = "folderName") String folderName) {
        String currentDirectory = userPathResolver.resolveUserS3Path(path);
        folderService.createEmptyFolder(currentDirectory, folderName);
        return resolveRedirectUrl(path);
    }

    @GetMapping("/move")
    public String getMovedFolderPath(@RequestParam(value = "movingPath") String movingPath,
                                     Model model) {
        List<TargetFolderDto> filtered = directoryListingService.listFoldersForFolderMove(movingPath);
        model.addAttribute("availableFoldersForFolderMove", filtered);

        return "fragments/folder-options :: options";
    }

    @PostMapping("/move")
    public String moveFolder(@ModelAttribute("moveFolderRequest") MoveFolderRequestDto request) {
        String currentFolderFullPath = request.getParentPath() + request.getName() + "/";
        String resolvedCurrentPath = userPathResolver.resolveUserS3Path(currentFolderFullPath);
        String resolvedTargetPath = userPathResolver.resolveUserS3Path(request.getTargetPath());

        folderService.moveFolder(resolvedCurrentPath, resolvedTargetPath);

        return resolveRedirectUrl(request.getParentPath());
    }

    private String resolveRedirectUrl(String path) {
        if (path == null) return "redirect:/home";

        String resolvedPath = userPathResolver.resolveUserS3Path(path);

        if (resolvedPath.replaceAll("/+$", "").trim().isEmpty()) return "redirect:/home";

        String encodedPath = userPathResolver.encodeUserS3Path(resolvedPath);
        return "redirect:/home?path=" + encodedPath;
    }
}
