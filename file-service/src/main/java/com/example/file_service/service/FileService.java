package com.example.file_service.service;

import com.example.file_service.config.AwsProperties;
import com.example.file_service.config.StorageProperties;
import com.example.file_service.dto.DownloadUrl;
import com.example.file_service.dto.FileResponse;
import com.example.file_service.dto.PublicUrlResponse;
import com.example.file_service.dto.UploadFileResponse;
import com.example.file_service.exception.FileNotFoundException;
import com.example.file_service.exception.UnsupportedFileTypeException;
import com.example.file_service.repository.FileMetaData;
import com.example.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final StorageProperties properties;
    private final FileRepository repository;
    private final AwsProperties awsProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;


    public UploadFileResponse uploadFile(MultipartFile file, long userId, FileCategory category) throws IOException {

        validate(file);


        String originalFileName = file.getOriginalFilename();
        String storageKey = generateStorageKey(originalFileName, category);


        FileMetaData fileMetaData;
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(awsProperties.bucket())
                            .key(storageKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );


            fileMetaData = new FileMetaData(
                    storageKey,
                    originalFileName,
                    file.getContentType(),
                    userId,
                    file.getSize(),
                    Instant.now(),
                    ObjectId.get()
            );

            log.info("Saving file metadata to database: userId={}, fileId={}, name={}",
                    fileMetaData.userId(), fileMetaData.fileId(), fileMetaData.originalName());

            repository.save(fileMetaData);

            log.info("File metadata successfuly saved: userId={}, fileId={}, name={}",
                    fileMetaData.userId(), fileMetaData.fileId(), fileMetaData.originalName());

        } catch (Exception e) {


            s3Client.deleteObject(DeleteObjectRequest
                    .builder()
                    .bucket(awsProperties.bucket())
                    .key(storageKey)
                    .build());

            throw new RuntimeException("Failed to upload file");
        }


        return new UploadFileResponse(fileMetaData.fileId().toHexString(), buildPublicUrl(storageKey));

    }



    private String generateStorageKey(String originalFileName, FileCategory category){


        String folder = switch (category){

            case AVATAR -> "avatars";
            case VIDEO -> "videos";
            case COURSE_COVER -> "course-covers";
            case ATTACHMENT -> "attachments";
        };

        return folder + "/" + UUID.randomUUID() + "-" + originalFileName;
    }


    private String buildPublicUrl(String storageKey){

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                awsProperties.bucket(),
                awsProperties.region(),
                storageKey);


    }







    private void validate(MultipartFile file){

        if(file.isEmpty()){
            throw new IllegalArgumentException("File is empty.");
        }

        String mimeType = file.getContentType();

        if(mimeType == null || !properties.allowedMimeTypes().contains(mimeType)){
            throw new UnsupportedFileTypeException("Unsupported file type.", mimeType, file.getOriginalFilename());
        }

    }

    public FileMetaData getFileMetaData(String fileId) {

        ObjectId objectId = new ObjectId(fileId);

        return repository.findById(objectId).orElseThrow(() -> new FileNotFoundException("File not found."));
    }

    public DownloadUrl generateDownloadUrl(String fileId) {

        FileMetaData fileMetaData = getFileMetaData(fileId);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsProperties.bucket())
                .key(fileMetaData.storageKey())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return new DownloadUrl(presignedRequest.url().toString());

    }



    public PublicUrlResponse getPublicUrl(String fileId){

        FileMetaData fileMetaData = getFileMetaData(fileId);

        String url = buildPublicUrl(fileMetaData.storageKey());

        return new PublicUrlResponse(url);


    }

    public List<FileMetaData> getUserFiles(long userId) {

        log.info("Fetching user files: userId={}", userId);

        List<FileMetaData> files = repository.findAllByUserId(userId);

        log.info("Fetched user files: userId={}, count={}", userId, files.size());

        return files;
    }

    public void deleteFileById(long userId, String fileId) throws IOException {

        if (!ObjectId.isValid(fileId)) {
            throw new IllegalArgumentException("Invalid file id");
        }

        ObjectId objectId = new ObjectId(fileId);
        log.info("Deleting file: userId={}, fileId={}", userId, fileId);

        FileMetaData fileMetaData = repository.findById(objectId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        if (fileMetaData.userId() != userId) {
            throw new AccessDeniedException("Access denied");
        }

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(awsProperties.bucket())
                        .key(fileMetaData.storageKey())
                        .build()
        );

        repository.deleteById(objectId);

        log.info("Deleted file: userId={}, fileId={}", userId, fileId);
    }
}
