package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.dto.S3ItemInfoDto;
import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.s3.MinIoService;
import com.aslmk.cloudfilestorage.util.UserSessionUtils;
import io.minio.errors.*;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
public class HomeController {
    private final MinIoService minIoService;
    private final UserSessionUtils userSessionUtils;

    public HomeController(MinIoService minIoService, UserSessionUtils userSessionUtils) {
        this.minIoService = minIoService;
        this.userSessionUtils = userSessionUtils;
    }

    @GetMapping("/home")
    public String homePage(@RequestParam(value = "path", required = false) String path,
                           Model model,
                           HttpSession session) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = userSessionUtils.getUserFromSession(session);

        String S3UserItemsPath = String.format("user-%s-files/", userEntity.getId());
        if (path != null && !path.isEmpty()) {
            S3UserItemsPath = UriComponentsBuilder.fromUriString(path)
                    .build()
                    .getPath();
        }
        List<S3ItemInfoDto> userItems = minIoService.getAllItems(S3UserItemsPath);
        model.addAttribute("userItems", userItems);

        return "home";
    }


    @PostMapping("/upload")
    public String uploadItem(@RequestParam("items") MultipartFile[] items,
                             HttpSession session,
                             @RequestParam(value = "path", required = false) String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = resolveUserS3Path(path, userEntity.getId());

        for (MultipartFile item: items) {
            String itemName = item.getOriginalFilename();
            InputStream itemStream = item.getInputStream();

            if (itemName == null || itemName.isBlank()) {
                throw new BadRequestException("Upload failed: No file or folder was provided");
            }

            minIoService.saveItem(S3UserItemsPath,itemName,itemStream);
        }

        return "redirect:/home?path="+ URLEncoder.encode(S3UserItemsPath, StandardCharsets.UTF_8);
    }
    @PostMapping("/remove")
    public String removeItem(@RequestParam("itemName") String itemName,
                             HttpSession session,
                             @RequestParam(value = "path", required = false) String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minIoService.removeItem(itemName);

        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = resolveUserS3Path(path, userEntity.getId());
        return "redirect:/home?path=" + URLEncoder.encode(S3UserItemsPath, StandardCharsets.UTF_8);
    }

    @PostMapping("/rename")
    public String renameItem(
            @RequestParam(value = "path", required = false) String path,
            @RequestParam("oldItemName") String oldItemName,
            @RequestParam("newItemName") String newItemName,
            HttpSession session) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        UserEntity userEntity = userSessionUtils.getUserFromSession(session);
        String S3UserItemsPath = resolveUserS3Path(path, userEntity.getId());

        if (newItemName.isBlank()) {
            throw new BadRequestException("Invalid file or folder name");
        }

        if (oldItemName.endsWith("/")) { // current item is a folder

            newItemName = newItemName.replaceAll("/+$","");

            newItemName += "/";

            if (!isFolderNameValid(newItemName)) {
                throw new BadRequestException("Invalid folder name");
            }
        } else {
            if (newItemName.contains("/")) {
                throw new BadRequestException("Invalid file name: cannot contain '/'");
            }
        }

        minIoService.renameItem(S3UserItemsPath, oldItemName, newItemName);

        return "redirect:/home?path=" + URLEncoder.encode(S3UserItemsPath, StandardCharsets.UTF_8);
    }

    private boolean isFolderNameValid(String folderName) {
        if (folderName == null || folderName.isBlank()) {
            return false;
        }

        return folderName.endsWith("/") && folderName.indexOf("/") == folderName.lastIndexOf("/");
    }

    private String resolveUserS3Path(String path, long userId) {
        String S3UserItemsPath = String.format("user-%s-files/", userId);
        if (path != null && !path.isEmpty()) {
            S3UserItemsPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
        }
        return S3UserItemsPath;
    }

}
