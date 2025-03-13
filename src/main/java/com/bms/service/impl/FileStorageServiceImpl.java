package com.bms.service.impl;

import com.bms.exception.BusinessException;
import com.bms.service.FileStorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.bms.util.CommonConstants.GCP_BUCKET;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Storage storage;

    @Value(GCP_BUCKET)
    private String bucketName;

    private static final String cloudStorageUrl = "https://storage.googleapis.com/%s/%s";

    public FileStorageServiceImpl(Storage storage) {
        this.storage = storage;
    }

    /**
     * This method is used to upload file to Google Cloud Storage
     *
     * @param file File to be uploaded
     * @return File URL
     */
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        return String.format(cloudStorageUrl, bucketName, fileName);
    }

    /**
     * This method is used to upload file to Google Cloud Storage
     *
     * @param file vehicle image
     * @param vehicleNo vehicle number
     */
    @Override
    public String uploadVehicleImage(MultipartFile file, String vehicleNo, String existingImage) throws IOException, BusinessException {

        if (existingImage != null && !existingImage.isEmpty()) {
            try {
                BlobId blobId = BlobId.of(bucketName, existingImage);
                storage.delete(blobId);
            } catch (Exception e) {
                throw new BusinessException("Error deleting file: " + HttpStatus.EXPECTATION_FAILED);
            }
        }

        String fileName = vehicleNo + "-" + file.getOriginalFilename();
        String path = "mcc/vehicles/" + fileName;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, path)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        return String.format(cloudStorageUrl, bucketName, path);
    }

    /**
     * This method is used to upload driver license to Google Cloud Storage
     */
    @Override
    public String uploadDriverLicense(MultipartFile file, String driverLicenseNo, String existingDrivingImage) throws IOException, BusinessException {

        if (existingDrivingImage != null && !existingDrivingImage.isEmpty()) {
            try {
                BlobId blobId = BlobId.of(bucketName, existingDrivingImage);
                storage.delete(blobId);
            } catch (Exception e) {
                throw new BusinessException("Error deleting file: " + existingDrivingImage, HttpStatus.EXPECTATION_FAILED);
            }
        }

        String fileName = driverLicenseNo + "-" + file.getOriginalFilename();
        String path = "mcc/driving_licenses/" + fileName;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, path)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        return String.format(cloudStorageUrl, bucketName, path);
    }

    /**
     * This method is used to upload file to Google Cloud Storage
     * for a specific folder
     *
     * @param file       File to be uploaded
     * @param folderName Folder name
     * @return File URL
     */
    @Override
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String objectName = (folderName != null && !folderName.isEmpty())
                ? folderName + "/" + fileName
                : fileName;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        return String.format(cloudStorageUrl, bucketName, objectName);
    }
}
