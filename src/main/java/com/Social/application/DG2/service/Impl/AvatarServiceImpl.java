package com.Social.application.DG2.service.Impl;

import com.Social.application.DG2.config.MinIOConfig;
import com.Social.application.DG2.entity.Users;
import com.Social.application.DG2.repositories.UsersRepository;
import com.Social.application.DG2.service.AvatarService;
import com.Social.application.DG2.util.exception.NotFoundException;
import io.minio.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final MinioClient minioClient;
    private final UsersRepository usersRepository;
    private final MinIOConfig minIOConfig;
    String bucketName = "avatar";
    String bucketName2 = "background";

    public AvatarServiceImpl(MinioClient minioClient, UsersRepository usersRepository, MinIOConfig minIOConfig) {
        this.minioClient = minioClient;
        this.usersRepository = usersRepository;
        this.minIOConfig = minIOConfig;
    }

    @Override
    public void uploadAvatar(MultipartFile file) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        try{
            // Kiểm tra bucketName
            checkBucketName(minioClient);

            try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
                String objectName = userId + "/" + file.getOriginalFilename();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(getContentType(objectName))
                                .build()

                );
                String avatarUrl = bucketName + "/" + objectName;
                currentUser.setAvatar(avatarUrl);
                usersRepository.save(currentUser);
            }
        } catch (Exception e) {
            throw new Exception("Lỗi khi tải tệp lên MinIO", e);
        }
    }
    @Override
    public void deleteAvatar(String objectName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        try {
            String filepath = userId + "/" + objectName;
            // Kiểm tra xem tệp tồn tại trên MinIO hay không
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );
            // Xóa tập tin từ MinIO
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            );
            currentUser.setAvatar(null);
            usersRepository.save(currentUser);
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new NotFoundException("Không tìm thấy tệp đính kèm từ MinIO: " + e.getMessage());
        }
    }

    @Override
    public void uploadBackground(MultipartFile file) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        try{
            // Kiểm tra bucketName
            checkBucketName2(minioClient);

            try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
                String objectName = userId + "/" + file.getOriginalFilename();
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName2)
                                .object(objectName)
                                .stream(inputStream, inputStream.available(), -1)
                                .contentType(getContentType(objectName))
                                .build()

                );
                String avatarUrl = bucketName2 + "/" + objectName;
                currentUser.setBackground(avatarUrl);
                usersRepository.save(currentUser);
            }
        } catch (Exception e) {
            throw new Exception("Lỗi khi tải tệp lên MinIO", e);
        }
    }

    @Override
    public void deleteBackground(String objectName) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);

        String userId = currentUser.getId();

        try {
            String filepath = userId + "/" + objectName;
            // Kiểm tra xem tệp tồn tại trên MinIO hay không
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName2)
                            .object(filepath)
                            .build()
            );
            // Xóa tập tin từ MinIO
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName2)
                            .object(filepath)
                            .build()
            );
            currentUser.setBackground(null);
            usersRepository.save(currentUser);
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
    public void checkBucketName2(MinioClient minioClient) throws Exception {

        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName2)
                .build();

        if (minioClient.bucketExists(bucketExistsArgs)) {
            System.out.println(bucketName2 + " exists.");
        } else {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucketName2)
                    .build();

            minioClient.makeBucket(makeBucketArgs);

            System.out.println(bucketName2 + " created.");
        }
    }
}