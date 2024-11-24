package com.ytdownloader.YoutubeDownloader;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/youtube")
public class YouTubeController {

    private static final String DOWNLOAD_DIR = "downloads/";

    // Endpoint to download video and return a link to the file
    @GetMapping("/download")
    public ResponseEntity<String> downloadVideo(@RequestParam String url) {
        try {
            // Ensure the download directory exists
            Files.createDirectories(Path.of(DOWNLOAD_DIR));

            // Generate yt-dlp command
            String command = String.format("yt-dlp -o %s%%(title)s.%%(ext)s %s", DOWNLOAD_DIR, url);

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // Get the downloaded file
            File[] files = new File(DOWNLOAD_DIR).listFiles();
            if (files != null && files.length > 0) {
                File videoFile = files[0]; // Assuming the first file is the downloaded video

                // Replace spaces with dashes in the file name
                String originalFileName = videoFile.getName();
                String modifiedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "");
                File modifiedFile = new File(DOWNLOAD_DIR + modifiedFileName);
                if (!originalFileName.equals(modifiedFileName)) {
                    videoFile.renameTo(modifiedFile);
                }

                // Return the link to the file
                String fileLink = String.format("http://localhost:8080/api/youtube/file/%s", modifiedFileName);
                return ResponseEntity.ok("Download successful! File link: " + fileLink);
            }

            return ResponseEntity.status(404).body("No video was downloaded.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // Endpoint to serve the downloaded file
    @GetMapping("/file/{fileName}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        try {
            File file = new File(DOWNLOAD_DIR + fileName);
            if (file.exists()) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
