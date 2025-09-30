package com.aslmk.cloudfilestorage;


import com.aslmk.cloudfilestorage.controller.SearchController;
import com.aslmk.cloudfilestorage.dto.SearchResultsDto;
import com.aslmk.cloudfilestorage.service.ItemSearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SearchControllerWebTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemSearchService itemSearchService;

    private static final String SEARCH_ENDPOINT = "/search";
    private static final String SEARCH_PAGE_VIEW_NAME = "search-page";
    private static final String REQUEST_PARAM = "query";
    private static final String MODEL_ATTRIBUTE_NAME = "searchResults";

    @Test
    void should_returnSearchPage_when_queryIsNull() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(SEARCH_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnSearchPageAndEmptySearchResults_when_queryIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
                        .param(REQUEST_PARAM, ""))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute(MODEL_ATTRIBUTE_NAME, Collections.emptyList()))
                .andExpect(MockMvcResultMatchers.view().name(SEARCH_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnSearchPageAndEmptySearchResults_when_queryContainsOnlySlashes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
                        .param(REQUEST_PARAM, "/////////////"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute(MODEL_ATTRIBUTE_NAME, Collections.emptyList()))
                .andExpect(MockMvcResultMatchers.view().name(SEARCH_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnSearchResults_when_queryIsValid() throws Exception {
        SearchResultsDto testResult = SearchResultsDto.builder()
                .itemName("testFile.txt")
                .isDirectory(false)
                .displayPath("/testFile.txt")
                .build();

        List<SearchResultsDto> searchResults = List.of(testResult);

        Mockito.when(itemSearchService.search(Mockito.anyString())).thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
                        .param(REQUEST_PARAM, "testFile.txt"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute(MODEL_ATTRIBUTE_NAME, searchResults))
                .andExpect(MockMvcResultMatchers.view().name(SEARCH_PAGE_VIEW_NAME));
    }

    @Test
    void should_returnSearchResults_when_queryIsInvalid() throws Exception {
        Mockito.when(itemSearchService.search(Mockito.anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get(SEARCH_ENDPOINT)
                        .param(REQUEST_PARAM, "af9h32f0fh2ldj!H#O!#HOH"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model()
                        .attribute(MODEL_ATTRIBUTE_NAME, Collections.emptyList()))
                .andExpect(MockMvcResultMatchers.view().name(SEARCH_PAGE_VIEW_NAME));
    }
}
