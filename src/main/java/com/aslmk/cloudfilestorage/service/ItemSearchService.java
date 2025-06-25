package com.aslmk.cloudfilestorage.service;

import com.aslmk.cloudfilestorage.dto.SearchResultsDto;

import java.util.List;

public interface ItemSearchService {
    List<SearchResultsDto> search(String query);
}
