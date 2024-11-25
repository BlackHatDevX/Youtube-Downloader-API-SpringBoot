package com.ytdownloader.YoutubeDownloader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/youtube")
public class YouTubeController {
    private static final String DOWNLOAD_DIR = "downloads/";

    // endpoint to download video and return the file link
    @GetMapping("/download")
    public ResponseEntity<String> downloadVideo(@RequestParam String url) {
        try {
            // CREATE DOWNLOAD DIRECTORY
            Files.createDirectories(Path.of(DOWNLOAD_DIR));

            // FINALIZE COMMAND TO DOWNLOAD YOUTUBE VIDEO WITH THE HELP OF YT-DLP
            String command = String.format("yt-dlp -o %s%%(title)s.%%(ext)s %s", DOWNLOAD_DIR, url);
            
            // EXECUTE THE COMMAND IN RUNTIME
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMessage = new String(process.getErrorStream().readAllBytes());
                return ResponseEntity.status(500).body("Error occurred while downloading: " + errorMessage);
            }
            
            

            // List all the files in download folder
            File[] files = new File(DOWNLOAD_DIR).listFiles();

            // first file is the downloaded file
            if (files != null && files.length>0){
                File videoFile = files[0];
                
                // Fix the name of file to remove special characters
                String originalFileName = videoFile.getName();
                String modifiedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
                File modifiedFile = new File(DOWNLOAD_DIR, modifiedFileName);
                if (!originalFileName.equals(modifiedFileName) && modifiedFile.exists()) {
                    modifiedFileName = System.currentTimeMillis() + "_" + modifiedFileName;
                    modifiedFile = new File(DOWNLOAD_DIR, modifiedFileName);
                }
                videoFile.renameTo(modifiedFile);
                

                // return the link to file
                String fileLink = String.format("http://localhost:8080/api/youtube/file/%s",modifiedFileName);
                return ResponseEntity.ok("Click here to download video : <a href="+fileLink+">"+originalFileName+"</a>");
            }

            // Print that the file is downloaded
            return( ResponseEntity.status(404).body("Error: Video isn't downloaded"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occured :"+ e.getMessage());
        }
    }

    @GetMapping("/file/{fileName}")
    public ResponseEntity serveFile(@PathVariable String fileName) {
        try {
            File file = new File(DOWNLOAD_DIR+fileName);
            if (!fileName.matches("[a-zA-Z0-9._-]+")) {
                return ResponseEntity.status(400).body("Invalid file name");
            }
            
            if (file.exists()){
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+file.getName()+"\"").body(resource);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Exception Error : "+e);
        }
        return ResponseEntity.ok("ok");
    }
    
    
    
}
