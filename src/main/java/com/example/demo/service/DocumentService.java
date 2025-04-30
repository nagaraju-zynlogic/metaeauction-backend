package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.bucket}")
    private String BUCKET_NAME;

    @Value("${supabase.api.key}")
    private String SUPABASE_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();
   

    // Upload file to Supabase Storage
    public String uploadFileToSupabase(MultipartFile file, String userId) {
        try {
            if (file.isEmpty()) {
                return "Upload failed: File is empty";
            }

            String fileName = file.getOriginalFilename();
            String pathInBucket = userId + "/" + fileName;

            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + pathInBucket;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
            headers.set("apikey", SUPABASE_API_KEY);
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));

            HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + pathInBucket;
            } else {
                return "Upload failed: " + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Upload error: " + e.getMessage();
        }
    }

    // Get all files in a specific user's folder
    public List<String> getAllFilesInFolder(String userId) {
        List<String> fileNames = new ArrayList<>();
        try {
            // Supabase list URL (POST request!)
            String listUrl = SUPABASE_URL + "/storage/v1/object/list/" + BUCKET_NAME;

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
            headers.set("apikey", SUPABASE_API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Body: send prefix = folder name (e.g., "1/")
            Map<String, Object> body = Map.of("prefix", userId + "/");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // Make POST call
            ResponseEntity<List> response = restTemplate.exchange(
                    listUrl,
                    HttpMethod.POST,
                    request,
                    List.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Cast the result safely (Map<String, Object>)
                for (Object obj : response.getBody()) {
                    if (obj instanceof Map) {
                        Map<?, ?> fileMap = (Map<?, ?>) obj;
                        String fileName = (String) fileMap.get("name");
                        fileNames.add(fileName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    // Download a document from Supabase
    public byte[] downloadDocument(String fileName) {
        try {
            String downloadUrl = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
            headers.set("apikey", SUPABASE_API_KEY);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.out.println("Download failed: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean deleteAllFilesInFolder(String folderPath) {
        try {
            List<String> fileNames = getAllFilesInFolder(folderPath);

            if (fileNames == null || fileNames.isEmpty()) {
                return true; // Nothing to delete
            }

            // Full paths with folder prefix
            List<String> fullPaths = fileNames.stream()
                .map(name -> folderPath + "/" + name)
                .collect(Collectors.toList());

            // Loop through and delete each file
            for (String path : fullPaths) {
                String deleteUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
                headers.set("apikey", SUPABASE_API_KEY);

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<Void> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Failed to delete file: " + path);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    
    
}