package com.bms.service;

import com.bms.util.BMSCheckedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    String uploadFile(MultipartFile file) throws IOException;

    String uploadVehicleImage(MultipartFile file, String vehicleNo, String existingImage) throws IOException, BMSCheckedException;

    String uploadFile(MultipartFile file, String folderName) throws IOException;
}
