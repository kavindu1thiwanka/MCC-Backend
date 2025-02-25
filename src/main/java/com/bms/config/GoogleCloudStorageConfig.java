package com.bms.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.io.InputStream;

import static com.bms.util.CommonConstants.GCP_PROJECT_ID;

@Configuration
public class GoogleCloudStorageConfig {

    @Value(GCP_PROJECT_ID)
    private String googleCloudProjectId;

    @Bean
    public Storage storage() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("gcs-key.json");
        return StorageOptions.newBuilder()
                .setProjectId(googleCloudProjectId)
                .setCredentials(ServiceAccountCredentials.fromStream(serviceAccount))
                .build()
                .getService();
    }
}
