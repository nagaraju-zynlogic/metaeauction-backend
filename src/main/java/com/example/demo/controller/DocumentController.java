package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Repository.userRepository;
import com.example.demo.entity.Users;
import com.example.demo.service.DocumentService;

@RestController
@RequestMapping("/documents")
public class DocumentController {
	

    @Value("${supabase.url}")
    private String SUPABASE_URL;

    @Value("${supabase.bucket}")
    private String BUCKET_NAME;

    @Autowired
    private DocumentService documentService;
    @Autowired
    private userRepository usersRepository;

    private final RestTemplate restTemplate = new RestTemplate(); // ✅ Added

    // Upload multiple files for a specific user
    @PostMapping("/upload/{userId}")
    public ResponseEntity<?> uploadFiles(
            @PathVariable String userId,
            @RequestParam("files") MultipartFile[] files) {

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No files uploaded.");
        }
        if (userId == null || userId.isEmpty()) {
			return ResponseEntity.badRequest().body("User ID is required.");
		}
       

        StringBuilder result = new StringBuilder();
        for (MultipartFile file : files) {
            String url = documentService.uploadFileToSupabase(file, userId);
            result.append("Uploaded to: ").append(url).append("\n");
            
            
        }
        // update user status to "pending"
        
        // user = usersRepository.findById(userId).orElse(null);
     //   Optional<Users> user = usersRepository.findAllById(userId).orElse(null);
        Optional<Users> user = usersRepository.findById(Integer.parseInt(userId));
        if (user.isPresent()) {
			Users userEntity = user.get();
			userEntity.setStatus("pending");
			usersRepository.save(userEntity);
		} else {
			return ResponseEntity.badRequest().body("User not found.");
		}
        return ResponseEntity.ok(new ApiResponse("Success", result.toString()));
    }

    // Download a document by userId and filename
    @GetMapping("/download/{userId}/{filename}")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable String userId,
            @PathVariable String filename) {

        String fullPath = userId + "/" + filename;
        byte[] fileData = documentService.downloadDocument(fullPath);

        if (fileData == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType contentType = getContentType(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(contentType)
                .body(fileData);
    }
    // List files for a user
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<String>> listFilesByUserId(@PathVariable String userId) {
        try {
            List<String> fileNames = documentService.getAllFilesInFolder(userId);

            if (fileNames == null || fileNames.isEmpty()) {
                return ResponseEntity.notFound().build(); // 404
            }

            return ResponseEntity.ok(fileNames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of("Error fetching files: " + e.getMessage()));
        }
    }

    // Return base64 content of all user documents
    @GetMapping("/data/{userId}")
    public ResponseEntity<Map<String, String>> getDownloadableLinksByUser(@PathVariable String userId) {
        List<String> fileNames = documentService.getAllFilesInFolder(userId);

        if (fileNames == null || fileNames.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Map of filename to download endpoint
        Map<String, String> downloadLinks = fileNames.stream()
                .collect(Collectors.toMap(
                        name -> name,
                        name -> "/documents/download/" + userId + "/" + name
                ));

        return ResponseEntity.ok(downloadLinks);
    }
    
    
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearUserFolder(@PathVariable String userId) {
        try {
            boolean success = documentService.deleteAllFilesInFolder(userId);
            if (success) {
            	// set user status to "rejected"
            	Optional<Users> user = usersRepository.findById(Integer.parseInt(userId));
            	if (user.isPresent()) {
            							Users userEntity = user.get();
            							userEntity.setStatus("rejected");
            							usersRepository.save(userEntity);
        		} else {
        								return ResponseEntity.badRequest().body("User not found.");
        		}
            	
                return ResponseEntity.ok("All files for user " + userId + " have been deleted.");
            } else {
                return ResponseEntity.status(500).body("Failed to delete files for user " + userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }



    // Detect content type from filename
	/*    private MediaType getContentType(String filename) {
	        if (filename.endsWith(".pdf")) {
	            return MediaType.APPLICATION_PDF;
	        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
	            return MediaType.IMAGE_JPEG;
	        } else if (filename.endsWith(".png")) {
	            return MediaType.IMAGE_PNG;
	        } else {
	            return MediaType.APPLICATION_OCTET_STREAM;
	        }
	    }*/
    private MediaType getContentType(String filename) {
        if (filename.toLowerCase().endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        } else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (filename.toLowerCase().endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
    
    
    // Response wrapper
    static class ApiResponse {
        private String status;
        private String message;

        public ApiResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}