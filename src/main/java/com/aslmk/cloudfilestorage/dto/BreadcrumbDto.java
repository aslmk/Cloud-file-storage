package com.aslmk.cloudfilestorage.dto;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BreadcrumbDto {
    private String name;
    private String fullPath;
}
