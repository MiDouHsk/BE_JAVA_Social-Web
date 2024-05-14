package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.config.MinIOConfig;
import com.Social.application.DG2.entity.Medias;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.MediaRepository;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.MediaService;
import com.Social.application.DG2.util.exception.NotFoundException;
import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinIOConfig minIOConfig;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MediaRepository mediaRepository;
    String bucketName = "posts";

    @Override
    public void createMedia(MultipartFile filePath) throws Exception {
//        String publicUrl = uploadMedia(filePath);
//
//        if (publicUrl == null || publicUrl.isEmpty()) {
//            throw new NotFoundException("not found Url");
//        }
//
//        Medias medias = new Medias();
//        medias.setId(UUID.randomUUID().toString());
//        medias.setBaseName(filePath.getName());
//        medias.setPublicUrl(objectName);
//
//        mediaRepository.save(medias);

    }

    @Override
    public String uploadMedia(MultipartFile filePath) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        try {
            // Kiểm tra bucketName
            checkBucketName(minioClient);

            try (InputStream inputStream = new BufferedInputStream(filePath.getInputStream())) {
                String originalFileName = filePath.getOriginalFilename();
                String objectName = userId + "/" + originalFileName;
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(getContentType(objectName))
                                .build()
                );
                String mediaFileUrl = bucketName + "/" + objectName;
                Medias medias = new Medias();
                String mediaId = UUID.randomUUID().toString();
                medias.setId(mediaId);
                medias.setBaseName(filePath.getOriginalFilename());
                medias.setPublicUrl(mediaFileUrl);

                mediaRepository.save(medias);

                return mediaId;

            }
        } catch (Exception e) {
            throw new Exception("Error uploading file to MinIO", e);
        }
    }


    @Override
    public void deletePost(String objectName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser.getId();

        try {
            String filepath = userId + "/" + objectName;

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new NotFoundException("Không tìm thấy tệp đính kèm từ MinIO: " + e.getMessage());
        }
    }


    private String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName).toLowerCase();
        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "wmv" -> "video/x-ms-wmv";
            default -> "application/octet-stream";
        };
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    public void checkBucketName(MinioClient minioClient) throws Exception {

        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();

        if (minioClient.bucketExists(bucketExistsArgs)) {
            System.out.println(bucketName + " exists.");
        } else {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build();

            minioClient.makeBucket(makeBucketArgs);

            System.out.println(bucketName + " created.");
        }
    }
}