package com.aslmk.cloudfilestorage.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultsDto {
    private String itemName;
    private String displayPath;
    private boolean isDirectory;
}
