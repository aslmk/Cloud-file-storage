package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.file.DownloadFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.MoveFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.RenameFileRequestDto;
import com.aslmk.cloudfilestorage.dto.file.UploadFileRequestDto;
import com.aslmk.cloudfilestorage.s3.FileService;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final UserPathResolver userPathResolver;

    public FileController(FileService fileService, UserPathResolver userPathResolver) {
        this.fileService = fileService;
        this.userPathResolver = userPathResolver;
    }


    @PostMapping("/upload")
    public String uploadFile(@ModelAttribute("uploadFileRequest") UploadFileRequestDto request,
                             @RequestParam(value = "path", required = false) String path) {
        request.setParentPath(userPathResolver.resolveUserS3Path(request.getParentPath()));
        fileService.saveFile(request);
        return resolveRedirectUrl(path);
    }

    @PostMapping("/rename")
    public String renameFile(@ModelAttribute("renameFileRequest") RenameFileRequestDto request,
                             @RequestParam(value = "path", required = false) String path) {

        String userRootFolder = userPathResolver.getUserRootFolder();
        request.setParentPath(userRootFolder + request.getParentPath());
        fileService.renameFile(request);

        return resolveRedirectUrl(path);
    }

    @PostMapping("/remove")
    public String removeFile(@RequestParam(value = "path", required = false) String path) {

        String fileFullPath = userPathResolver.getUserRootFolder() + path;

        fileService.removeFile(fileFullPath);

        return resolveRedirectUrl(new S3Path(path).getParentPath());
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(
            @ModelAttribute("downloadFileRequest") DownloadFileRequestDto request) {
        request.setParentPath(userPathResolver.resolveUserS3Path(request.getParentPath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=\"%s\"", request.getFileName()))
                .body(fileService.downloadFile(request));
    }

    @PostMapping("/move")
    public String moveFile(@ModelAttribute("moveFileRequest") MoveFileRequestDto request) {
        String resolvedCurrentPath = userPathResolver.resolveUserS3Path(request.getParentPath());
        String resolvedTargetPath = userPathResolver.resolveUserS3Path(request.getTargetPath());

        String newFilePath = resolvedTargetPath + request.getName();
        String currentPath = resolvedCurrentPath + request.getName();

        fileService.moveFile(currentPath, newFilePath);

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
