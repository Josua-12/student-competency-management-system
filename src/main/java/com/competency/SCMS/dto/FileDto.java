package com.competency.SCMS.dto;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class FileDto {
    Long id;
    Long groupId;
    String name;
    Long size;         // bytes
    String uploadedAt; // yyyy-MM-dd
    String url;        // /files/{grp}/{id}
}
