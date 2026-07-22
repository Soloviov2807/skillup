package com.example.file_service.mapper;

import com.example.file_service.dto.FileResponse;
import com.example.file_service.repository.FileMetaData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileMapper {

    String rootUrl = "/files/get/";


    public FileResponse mapToResponse(FileMetaData fileMetaData){

        return new FileResponse(
                fileMetaData.originalName(),
                fileMetaData.fileId().toHexString(),
                rootUrl + fileMetaData.fileId().toHexString(),
                fileMetaData.mimeType(), fileMetaData.createdAt(), fileMetaData.size());

    }

    public List<FileResponse> mapToListResponses(List<FileMetaData> filesMetadata){

        return filesMetadata.stream()
                .map(v -> new FileResponse(
                        v.originalName(),
                        v.fileId().toHexString(),
                        rootUrl + v.fileId().toHexString(),
                        v.mimeType(),
                        v.createdAt(),
                        v.size()))
                .toList();
    }

}
