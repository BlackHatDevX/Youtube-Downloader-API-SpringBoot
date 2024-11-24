# YouTube Video Downloader API

This project is a Spring Boot application that provides an API for downloading YouTube videos and serving them via HTTP. It uses the powerful `yt-dlp` tool to fetch video files and processes them to ensure safe and clean file names.

## Features

- **Download YouTube Videos**: Provide a YouTube video URL, and the application downloads the video to a local directory.
- **Safe File Names**: Automatically sanitizes file names by removing invalid characters, keeping only alphanumeric characters to ensure compatibility with URLs and file systems.
- **Serve Files via API**: Downloaded videos can be accessed via a REST endpoint.

## Requirements

Before running this application, ensure you have:

- Java 17 or later
- Spring Boot
- `yt-dlp` installed and available in your system's PATH
  (

`brew install yt-dlp`
OR
`pip install yt-dlp`
OR
`pip3 install yt-dlp`

## Endpoints

### 1. Download Video

**Endpoint**: `/api/youtube/download`  
**Method**: `GET`  
**Parameters**:

- `url` (required): The YouTube video URL

**Example Request**:

```http
GET http://localhost:8080/api/youtube/download?url=https://www.youtube.com/watch?v=example
```

**Response**:

- On success: Returns a message with a download link.
- On failure: Returns an error message.

---

### 2. Serve File

**Endpoint**: `/api/youtube/file/{fileName}`  
**Method**: `GET`

**Example Request**:

```http
GET http://localhost:8080/api/youtube/file/examplevideo.mp4
```

**Response**:

- Returns the requested video file for download, or a `404` status if the file is not found.

---

## How It Works

1. **Download Directory**: Videos are downloaded to the `downloads/` folder in the project directory.
2. **File Name Sanitization**: File names are sanitized to include only alphanumeric characters by removing spaces, special symbols, and emojis.
3. **Serving Files**: Files are served using Spring's `FileSystemResource`.

## Running the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/BlackHatDevX/youtube-downloader-api-springboot.git
   cd youtube-downloader-api-springboot
   ```

2. Start the application:

   ```bash
   ./mvnw spring-boot:run
   ```

3. Make API requests using tools like Postman, Curl, or directly via the browser.

## Sanitization Logic

The application ensures that all file names:

- Contain only letters (`a-z`, `A-Z`) and numbers (`0-9`).
- Exclude spaces, special characters, and emojis.

This prevents issues with URLs and file systems while ensuring compatibility across different platforms.

## Example Output

**Input File Name**: `My Video Title - YouTube 😎.mp4`  
**Sanitized File Name**: `MyVideoTitleYouTube.mp4`

## License

This project is licensed under the MIT License.
