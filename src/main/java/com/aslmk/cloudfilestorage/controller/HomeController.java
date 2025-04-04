package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.s3.MinIoService;
import com.aslmk.cloudfilestorage.service.UserService;
import io.minio.errors.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserService userService;
    private final MinIoService minIoService;

    public HomeController(UserService userService, MinIoService minIoService) {
        this.userService = userService;
        this.minIoService = minIoService;
    }


    @GetMapping("/home")
    public String homePage(Principal principal, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String currentUser = principal.getName();
        Optional<UserEntity> user = userService.findByUsername(currentUser);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        UserEntity userEntity = user.get();

        String bucketName = String.format("user-%s-files", userEntity.getId());

        List<String> userFiles = minIoService.getAllFiles(bucketName);
        model.addAttribute("userFiles", userFiles);

        return "home";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Principal principal, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String currentUser = principal.getName();
        Optional<UserEntity> user = userService.findByUsername(currentUser);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        UserEntity userEntity = user.get();

        String S3filePath = String.format("user-%s-files", userEntity.getId());
        String filename = file.getOriginalFilename();
        InputStream fileStream = file.getInputStream();

        minIoService.saveFile(S3filePath,filename,fileStream);

       return "redirect:/home";
    }
    @PostMapping("/remove")
    public String remove(@RequestParam("fileName") String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minIoService.removeFile(fileName);
        return "redirect:/home";
    }

    @PostMapping("/rename")
    public String fileRename(@RequestParam("oldFileName") String oldFileName,
                             @RequestParam("newFileName") String newFileName,
                             Principal principal) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = getUserFromPrincipal(principal);
        String S3filePath = String.format("user-%s-files", userEntity.getId());
        minIoService.renameFile(S3filePath, oldFileName, newFileName);
        return "redirect:/home";
    }

    private UserEntity getUserFromPrincipal(Principal principal) {
        String currentUser = principal.getName();
        Optional<UserEntity> user = userService.findByUsername(currentUser);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found");
        }

        return user.get();
    }

}
