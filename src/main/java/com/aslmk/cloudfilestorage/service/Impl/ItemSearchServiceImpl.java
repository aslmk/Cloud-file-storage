package com.aslmk.cloudfilestorage.service.Impl;

import com.aslmk.cloudfilestorage.dto.S3Path;
import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.service.DirectoryListingService;
import com.aslmk.cloudfilestorage.service.ItemSearchService;
import com.aslmk.cloudfilestorage.util.UserPathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Value("${empty-folder-marker}")
    private String EMPTY_FOLDER;

    private final UserPathResolver userPathResolver;
    private final DirectoryListingService directoryListingService;

    public ItemSearchServiceImpl(UserPathResolver userPathResolver, DirectoryListingService directoryListingService) {
        this.userPathResolver = userPathResolver;
        this.directoryListingService = directoryListingService;
    }


    @Override
    public List<SearchResultsDto> search(String query) {
        String userRootFolder = userPathResolver.getUserRootFolder();
        List<S3Path> allItems = directoryListingService.listS3Paths(userRootFolder, true);
        return filterMatchingItems(allItems, query);
    }

    private List<SearchResultsDto> filterMatchingItems(List<S3Path> allItems, String query) {
        String normalizedQuery = query.endsWith("/") ? query.substring(0, query.length() - 1) : query;
        Set<String> seenPaths = new HashSet<>();
        List<SearchResultsDto> results = new ArrayList<>();
        for (S3Path item : allItems) {
            String itemName = item.getItemName();
            String absolutePath = item.absolutePath();

            boolean isEmptyFolder = absolutePath.endsWith(EMPTY_FOLDER);
            String parentPath = isEmptyFolder ?
                    new S3Path(item.getParentPath()).getParentPath() :
                    item.getParentPath();

            boolean isDir = query.endsWith("/");
            boolean nameMatches = itemName.contains(normalizedQuery);
            boolean parentMatches = isDir && item.getLastFolderName().contains(normalizedQuery);

            if (!isDir && nameMatches && !isEmptyFolder && seenPaths.add(absolutePath)) {
                results.add(buildResult(itemName, parentPath, false));
            }

            if (isDir && parentMatches && isEmptyFolder && seenPaths.add(parentPath)) {

                String folderName = item.getLastFolderName();
                results.add(buildResult(folderName, parentPath, true));
            }
        }

        return results;
    }
    private SearchResultsDto buildResult(String name, String path, boolean isDirectory) {
        String userRootFolder = userPathResolver.getUserRootFolder();
        String newPath = path.replace(userRootFolder, "/");
        return SearchResultsDto.builder()
                .itemName(name)
                .displayPath(newPath)
                .isDirectory(isDirectory)
                .build();
    }
}
