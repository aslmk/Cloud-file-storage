package com.aslmk.cloudfilestorage;

import com.aslmk.cloudfilestorage.dto.BreadcrumbDto;
import com.aslmk.cloudfilestorage.util.BreadcrumbUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BreadcrumbUtilUnitTests {

    private final BreadcrumbUtil breadcrumbUtil = new BreadcrumbUtil();

    private static final String ENCODED_SLASH_SYMBOL = "%2F";
    private static final String EMPTY_STRING = "";
    private static final String SPACE_CHARACTER = " ";
    private static final String PATH = "folder1%2Ffolder2%2Ffolder3%2Ffolder4%2Ffolder5";

    private List<BreadcrumbDto> expectedBreadcrumbs;

    @BeforeEach
    public void setUp() {
        expectedBreadcrumbs = new ArrayList<>();

        StringBuilder folderPath = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            folderPath.append("folder").append(i).append(ENCODED_SLASH_SYMBOL);

            BreadcrumbDto dto = new BreadcrumbDto();
            dto.setName("folder" + i);
            dto.setFullPath(folderPath.toString());

            expectedBreadcrumbs.add(dto);
        }
    }

    @Test
    void should_returnEmptyList_when_pathIsNull() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(null);

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void should_returnEmptyList_when_pathIsEmptyString() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(EMPTY_STRING);

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void should_returnEmptyList_when_pathContainsOnlySlashes() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb("////");

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void should_returnEmptyList_when_pathIsSpaceCharacter() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(SPACE_CHARACTER);

        Assertions.assertTrue(actual.isEmpty());
    }

    @Test
    void should_returnListOfBreadcrumbDtos_when_pathIsValid() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(PATH);

        Assertions.assertTrue(isListsEqual(expectedBreadcrumbs, actual));
    }

    @Test
    void should_returnListOfBreadcrumbDtos_when_pathIsValidAndStartsWithSlash() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(ENCODED_SLASH_SYMBOL + PATH);

        Assertions.assertTrue(isListsEqual(expectedBreadcrumbs, actual));
    }

    @Test
    void should_returnListOfBreadcrumbDtos_when_pathContainsMultipleSlashes() {
        String path = "folder1%2Ffolder2%2Ffolder3%2F%2F%2F%2Ffolder4%2Ffolder5";
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(path);

        Assertions.assertTrue(isListsEqual(expectedBreadcrumbs, actual));
    }

    @Test
    void should_returnListOfBreadcrumbDtos_when_pathContainsSpecialCharacters() {
        String path = "folder1%2Ffolder2++  %2Ffolder3%2Ffolder4?!!!%2Ffolder5";

        expectedBreadcrumbs = new ArrayList<>();
        StringBuilder folderPath = new StringBuilder();
        String folderName;
        for (int i = 1; i <= 5; i++) {
            if (i == 2) {
                folderPath.append("folder").append(i).append("++  ");
                folderName = "folder" + i + "++  ";
            } else if (i == 4) {
                folderPath.append("folder").append(i).append("?!!!");
                folderName = "folder" + i + "?!!!";
            } else {
                folderPath.append("folder").append(i);
                folderName = "folder" + i;
            }

            folderPath.append(ENCODED_SLASH_SYMBOL);

            BreadcrumbDto dto = new BreadcrumbDto();
            dto.setName(folderName);
            dto.setFullPath(folderPath.toString());

            expectedBreadcrumbs.add(dto);
        }

        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(path);

        Assertions.assertTrue(isListsEqual(expectedBreadcrumbs, actual));
    }

    @Test
    void should_returnListOfBreadcrumbDtos() {
        List<BreadcrumbDto> actual = breadcrumbUtil.getBreadcrumb(PATH + ENCODED_SLASH_SYMBOL);
        Assertions.assertTrue(isListsEqual(expectedBreadcrumbs, actual));
    }

    private boolean isListsEqual(List<BreadcrumbDto> list1, List<BreadcrumbDto> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < 5; i++) {
            String list1ElementName = list1.get(i).getName();
            String list2ElementName = list2.get(i).getName();

            String list1ElementFullPath = list1.get(i).getFullPath();
            String list2ElementFullPath = list2.get(i).getFullPath();

            if (!list1ElementName.equals(list2ElementName)) {
                return false;
            }
            if (!list1ElementFullPath.equals(list2ElementFullPath)) {
                return false;
            }
        }

        return true;
    }
}
