package com.aslmk.cloudfilestorage;


import com.aslmk.cloudfilestorage.entity.UserEntity;
import com.aslmk.cloudfilestorage.exception.UnauthorizedAccessException;
import com.aslmk.cloudfilestorage.security.CustomUserDetails;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
public class UserPathResolverUT {

    private static final String USER_ROOT_FOLDER = "user-6-files/";
    private static final String EMPTY_STRING = "";
    private static final String EXPECTED_RESOLVED_PATH = "user-6-files/folder1/folder2/folder3";
    private static final String EXPECTED_ENCODED_PATH = "folder1%2Ffolder2%2Ffolder3";

    @Spy
    @InjectMocks
    private UserPathResolver resolver;

    @BeforeEach
    void setUp() {
        lenient().doReturn(USER_ROOT_FOLDER).when(resolver).getUserRootFolder();
    }

    @Test
    void resolveUserS3Path_should_returnRootFolder_when_pathIsNull() {
        String actualPath = resolver.resolveUserS3Path(null);

        Assertions.assertEquals(USER_ROOT_FOLDER, actualPath);
    }

    @Test
    void resolveUserS3Path_should_returnRootFolder_when_pathIsEmpty() {
        String actualPath = resolver.resolveUserS3Path(EMPTY_STRING);

        Assertions.assertEquals(USER_ROOT_FOLDER, actualPath);
    }

    @Test
    void resolveUserS3Path_should_returnRootFolder_when_pathIsSpaceCharacter() {
        String actualPath = resolver.resolveUserS3Path(" ");

        Assertions.assertEquals(USER_ROOT_FOLDER, actualPath);
    }

    @Test
    void resolveUserS3Path_should_returnRootFolder_when_pathIsOnlySlash() {
        String actualPath = resolver.resolveUserS3Path("/");

        Assertions.assertEquals(USER_ROOT_FOLDER, actualPath);
    }

    @Test
    void resolveUserS3Path_should_returnRootFolder_when_pathContainsOnlySlashes() {
        String actualPath = resolver.resolveUserS3Path("///////");

        Assertions.assertEquals(USER_ROOT_FOLDER, actualPath);
    }

    @Test
    void resolveUserS3Path_should_appendRootFolderAndReturnFullPath_when_pathIsValid() {
        String path = "folder1/folder2/folder3";

        String actualPath = resolver.resolveUserS3Path(path);

        Assertions.assertEquals(EXPECTED_RESOLVED_PATH, actualPath);
    }

    @Test
    void resolveUserS3Path_should_returnFullPathWithoutLeadingSlash_when_pathStartsWithSlash() {
        String path = "/folder1/folder2/folder3";

        String actualPath = resolver.resolveUserS3Path(path);

        Assertions.assertEquals(EXPECTED_RESOLVED_PATH, actualPath);
    }

    @Test
    void resolveUserS3Path_should_replaceMultipleSlashesWithOneAndReturnFullPath_when_pathIsValid() {
        String path = "///////folder1///folder2////folder3";

        String actualPath = resolver.resolveUserS3Path(path);

        Assertions.assertEquals(EXPECTED_RESOLVED_PATH, actualPath);
    }

    @Test
    void resolveUserS3Path_should_decodePathAndReturnEncodedFullPath_when_pathIsEncoded() {
        String path = "folder1%2Ffolder2%2Ffolder3";

        String actualPath = resolver.resolveUserS3Path(path);

        Assertions.assertEquals(EXPECTED_RESOLVED_PATH, actualPath);
    }

    @Test
    void resolveUserS3Path_should_returnValidFullPath_when_pathContainsSpecialCharacters() {
        String path = "/folder 1/folder2!!!/folder3??";
        String actualPath = resolver.resolveUserS3Path(path);

        String expectedPath = "user-6-files/folder 1/folder2!!!/folder3??";

        Assertions.assertEquals(expectedPath, actualPath);
    }

    @Test
    void resolveUserS3Path_should_ReturnValidFullPath_when_pathContainsUnicodeSymbols() {
        String path = "\uD83D\uDC31my_folder";
        String actualPath = resolver.resolveUserS3Path(path);

        String expectedPath = "user-6-files/\uD83D\uDC31my_folder";

        Assertions.assertEquals(expectedPath, actualPath);
    }

    @Test
    void resolveUserS3Path_should_ReturnDecodedFullPath_after_encodeUserS3Path_encodesThePath() {
        String path = "folder1/folder2/folder3";

        String encodedPath = resolver.encodeUserS3Path(path);

        String actualPath = resolver.resolveUserS3Path(encodedPath);

        Assertions.assertEquals(EXPECTED_RESOLVED_PATH, actualPath);
    }

    @Test
    void resolveUserS3Path_should_ReturnDecodedFullPath_after_encodeUserS3Path_encodesThePathWithSpecialCharacters() {
        String path = "folder1%2Ffolder2/folder3!!!";

        String encodedPath = resolver.encodeUserS3Path(path);

        String actualPath = resolver.resolveUserS3Path(encodedPath);

        String expectedPath = "user-6-files/folder1/folder2/folder3!!!";

        Assertions.assertEquals(expectedPath, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEmptyString_when_pathIsNull() {
        String actualPath = resolver.encodeUserS3Path(null);

        Assertions.assertEquals(EMPTY_STRING, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEmptyString_when_pathIsEmpty() {
        String actualPath = resolver.encodeUserS3Path(EMPTY_STRING);

        Assertions.assertEquals(EMPTY_STRING, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEmptyString_when_pathContainsOnlySlashes() {
        String actualPath = resolver.encodeUserS3Path("/////");

        Assertions.assertEquals(EMPTY_STRING, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEmptyString_when_pathIsSpaceCharacter() {
        String actualPath = resolver.encodeUserS3Path(" ");

        Assertions.assertEquals(EMPTY_STRING, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEmptyString_when_pathIsRootFolder() {
        String actualPath = resolver.encodeUserS3Path(USER_ROOT_FOLDER);

        Assertions.assertEquals(EMPTY_STRING, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEncodedPath_when_pathIsValid() {
        String path = "folder1/folder2/folder3";

        String actualPath = resolver.encodeUserS3Path(path);

        Assertions.assertEquals(EXPECTED_ENCODED_PATH, actualPath);
    }

    @Test
    void encodeUserS3Path_should_EncodePathAndReturnEncodedPathWithoutRootFolder_when_pathIsValid() {
        String path = "user-6-files/folder1/folder2/folder3";

        String actualPath = resolver.encodeUserS3Path(path);

        Assertions.assertEquals(EXPECTED_ENCODED_PATH, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnValidEncodedPath_when_pathContainsSpecialCharacters() {
        String path = "user-6-files///folder+1/folder2???/folder3!!!";

        String actualPath = resolver.encodeUserS3Path(path);

        String expectedPath = "folder%2B1%2Ffolder2%3F%3F%3F%2Ffolder3%21%21%21";

        Assertions.assertEquals(expectedPath, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEncodedFullPath_after_resolveS3Path_decodesThePath() {
        String path = "folder1%2Ffolder2%2Ffolder3";

        String decodedPath = resolver.resolveUserS3Path(path);

        String actualPath = resolver.encodeUserS3Path(decodedPath);

        Assertions.assertEquals(EXPECTED_ENCODED_PATH, actualPath);
    }

    @Test
    void encodeUserS3Path_should_ReturnEncodedFullPath_after_resolveS3Path_decodesThePathWithSpecialCharacters() {
        String path = "folder1%2Ffolder2/folder3!!!";

        String decodedPath = resolver.resolveUserS3Path(path);

        String actualPath = resolver.encodeUserS3Path(decodedPath);

        String expectedPath = "folder1%2Ffolder2%2Ffolder3%21%21%21";

        Assertions.assertEquals(expectedPath, actualPath);
    }

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserRootFolder_should_ReturnRootFolder_when_userIsAuthenticated() {
        Authentication authentication = createAuthenticationToken();

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        Mockito.reset(resolver);
        doCallRealMethod().when(resolver).getUserRootFolder();
        String actualPath = resolver.getUserRootFolder();

        Assertions.assertNotNull(actualPath);
        Assertions.assertEquals(USER_ROOT_FOLDER, actualPath);
    }

    @Test
    void getUserRootFolder_should_ThrowUnauthorizedException_when_authenticationIsNull() {
        SecurityContextHolder.clearContext();

        Mockito.reset(resolver);
        doCallRealMethod().when(resolver).getUserRootFolder();

        Assertions.assertThrows(UnauthorizedAccessException.class, () -> resolver.getUserRootFolder());
    }

    @Test
    void getUserRootFolder_should_ThrowUnauthorizedException_when_userIsNotAuthenticated() {
        Authentication authentication = createAuthenticationToken();
        authentication.setAuthenticated(false);

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        Mockito.reset(resolver);
        doCallRealMethod().when(resolver).getUserRootFolder();

        Assertions.assertThrows(UnauthorizedAccessException.class, () -> resolver.getUserRootFolder());
    }

    @Test
    void getUserRootFolder_should_ThrowIllegalStateException_when_principalIsNotInstanceOfCustomUserDetails() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Authentication authentication = UsernamePasswordAuthenticationToken
                .authenticated(userDetails, null, Collections.emptyList());

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        Mockito.reset(resolver);
        doCallRealMethod().when(resolver).getUserRootFolder();

        Assertions.assertThrows(IllegalStateException.class, () -> resolver.getUserRootFolder());
    }

    private Authentication createAuthenticationToken() {
        UserEntity userEntity = UserEntity.builder().id(6L).build();
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        return new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                Collections.emptyList());
    }
}