package com.bms.service;

import com.bms.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    String uploadFile(MultipartFile file) throws IOException;

    String uploadVehicleImage(MultipartFile file, String vehicleNo, String existingImage) throws IOException, BusinessException;

    String uploadDriverLicense(MultipartFile file, String driverLicenseNo, String existingImage) throws IOException, BusinessException;

    String uploadFile(MultipartFile file, String folderName) throws IOException;
}
