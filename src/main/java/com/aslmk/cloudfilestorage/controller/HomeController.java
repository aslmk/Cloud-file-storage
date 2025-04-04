package com.aslmk.cloudfilestorage.controller;

import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.exception.AccessDeniedException;
import com.aslmk.cloudfilestorage.s3.MinIoService;
import com.aslmk.cloudfilestorage.service.UserService;
import io.minio.errors.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String redirectToPersonalFilesPage(Principal principal) {
        UserEntity userEntity = getUserFromPrincipal(principal);
        String S3UserFilesPath = String.format("user-%s-files", userEntity.getId());

        return "redirect:/"+S3UserFilesPath+"/home";
    }

    @GetMapping("/user-{userId}-files/home")
    public String homePage(@PathVariable("userId") long userId, Principal principal, Model model) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = getUserFromPrincipal(principal);

        if (userEntity.getId() != userId) {
            throw new AccessDeniedException("You are not allowed to access this URL");
        }

        String S3UserFilesPath = String.format("user-%s-files", userEntity.getId());
        List<String> userFiles = minIoService.getAllFiles(S3UserFilesPath);
        model.addAttribute("userFiles", userFiles);

        return "home";
    }

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file, Principal principal) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = getUserFromPrincipal(principal);
        String S3filePath = String.format("user-%s-files", userEntity.getId());
        String filename = file.getOriginalFilename();
        InputStream fileStream = file.getInputStream();

        minIoService.saveFile(S3filePath,filename,fileStream);

        return "redirect:/home";
    }
    @PostMapping("/remove")
    public String fileRemove(@RequestParam("fileName") String fileName,
                             Principal principal) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        UserEntity userEntity = getUserFromPrincipal(principal);
        String S3filePath = String.format("user-%s-files", userEntity.getId());
        minIoService.removeFile(S3filePath + "/" + fileName);
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
