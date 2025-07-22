//package com.example.demo.service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//public class DocumentService {
//
//    @Value("${supabase.url}")
//    private String SUPABASE_URL;
//
//    @Value("${supabase.bucket}")
//    private String BUCKET_NAME;
//
//    @Value("${supabase.api.key}")
//    private String SUPABASE_API_KEY;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//   
//
//    // Upload file to Supabase Storage
//    public String uploadFileToSupabase(MultipartFile file, String userId) {
//        try {
//            if (file.isEmpty()) {
//                return "Upload failed: File is empty";
//            }
//
//            String fileName = file.getOriginalFilename();
//            String pathInBucket = userId + "/" + fileName;
//
//            String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + pathInBucket;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
//            headers.set("apikey", SUPABASE_API_KEY);
//            headers.setContentType(MediaType.parseMediaType(file.getContentType()));
//
//            HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
//
//            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.PUT, entity, String.class);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                return SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + pathInBucket;
//            } else {
//                return "Upload failed: " + response.getStatusCode();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Upload error: " + e.getMessage();
//        }
//    }
//
//    // Get all files in a specific user's folder
//    public List<String> getAllFilesInFolder(String userId) {
//        List<String> fileNames = new ArrayList<>();
//        try {
//            // Supabase list URL (POST request!)
//            String listUrl = SUPABASE_URL + "/storage/v1/object/list/" + BUCKET_NAME;
//
//            // Headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
//            headers.set("apikey", SUPABASE_API_KEY);
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            // Body: send prefix = folder name (e.g., "1/")
//            Map<String, Object> body = Map.of("prefix", userId + "/");
//
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//
//            // Make POST call
//            ResponseEntity<List> response = restTemplate.exchange(
//                    listUrl,
//                    HttpMethod.POST,
//                    request,
//                    List.class
//            );
//
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                // Cast the result safely (Map<String, Object>)
//                for (Object obj : response.getBody()) {
//                    if (obj instanceof Map) {
//                        Map<?, ?> fileMap = (Map<?, ?>) obj;
//                        String fileName = (String) fileMap.get("name");
//                        fileNames.add(fileName);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fileNames;
//    }
//
//    // Download a document from Supabase
//    public byte[] downloadDocument(String fileName) {
//        try {
//            String downloadUrl = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
//            headers.set("apikey", SUPABASE_API_KEY);
//
//            HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<byte[]> response = restTemplate.exchange(downloadUrl, HttpMethod.GET, entity, byte[].class);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                return response.getBody();
//            } else {
//                System.out.println("Download failed: " + response.getStatusCode());
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    
//    public boolean deleteAllFilesInFolder(String folderPath) {
//        try {
//            List<String> fileNames = getAllFilesInFolder(folderPath);
//
//            if (fileNames == null || fileNames.isEmpty()) {
//                return true; // Nothing to delete
//            }
//
//            // Full paths with folder prefix
//            List<String> fullPaths = fileNames.stream()
//                .map(name -> folderPath + "/" + name)
//                .collect(Collectors.toList());
//
//            // Loop through and delete each file
//            for (String path : fullPaths) {
//                String deleteUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
//                headers.set("apikey", SUPABASE_API_KEY);
//
//                HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//                ResponseEntity<Void> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, Void.class);
//
//                if (!response.getStatusCode().is2xxSuccessful()) {
//                    System.out.println("Failed to delete file: " + path);
//                    return false;
//                }
//            }
//
//            return true;
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//  
//}








package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentService {

    private final Path fileStorageLocation;

    // The constructor now sets up the base storage directory
    public DocumentService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // Create the base directory if it doesn't exist
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Saves a file to a user-specific folder.
     */
    public void saveFile(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // 1. Create the user-specific directory path (e.g., ./user-documents/123)
        Path userDirectory = this.fileStorageLocation.resolve(userId);
        Files.createDirectories(userDirectory); // Create folder if it doesn't exist

        // 2. Resolve the final file path and copy the file
        Path targetLocation = userDirectory.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Reads a file's bytes from a user's folder.
     */
    public byte[] downloadDocument(String userId, String filename) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(userId).resolve(filename);
        if (!Files.exists(filePath)) {
            return null; // Or throw a FileNotFoundException
        }
        return Files.readAllBytes(filePath);
    }

    /**
     * Lists all filenames in a user's folder.
     */
    public List<String> getAllFilesInFolder(String userId) {
        Path userDirectory = this.fileStorageLocation.resolve(userId);
        if (!Files.exists(userDirectory)) {
            return Collections.emptyList(); // No folder means no files
        }

        try (Stream<Path> stream = Files.list(userDirectory)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return empty on error
        }
    }

    /**
     * Deletes a user's entire folder and all its contents.
     */
    public boolean deleteAllFilesInFolder(String userId) {
        try {
            Path userDirectory = this.fileStorageLocation.resolve(userId);
            if (Files.exists(userDirectory)) {
                // FileSystemUtils.deleteRecursively is a robust way to delete a folder
                return FileSystemUtils.deleteRecursively(userDirectory);
            }
            return true; // Folder didn't exist, so it's "deleted"
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}