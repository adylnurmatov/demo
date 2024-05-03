package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class VideoUploadController {

//    @GetMapping("/")
//    public String index() {
//        return "upload";
//    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a video file to upload.");
            return "upload";
        }
        try {
            // Сохранение файла на сервере
            String filename = file.getOriginalFilename();
            Path uploadDir = ensureUploadsDir();
            Path path = uploadDir.resolve(filename);
            file.transferTo(path.toFile());

            // Запуск скрипта Python для анализа видео
            ProcessBuilder builder = new ProcessBuilder("python", "src/main/resources/face_recognition_script.py", path.toString());
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean matchFound = false;

            while ((line = reader.readLine()) != null) {
                if ("Match found.".equals(line)) {
                    matchFound = true;
                    break;
                }
            }
            process.waitFor();

            if (matchFound) {
                model.addAttribute("message", "A matching face has been found in the video.");
            } else {
                model.addAttribute("message", "No matching faces were found.");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Failed to upload and process the video: " + e.getMessage());
        }

        return "upload";

    }
        private static final String UPLOAD_DIR = "C:/Users/User/IdeaProjects/demo1/src/main/resources/static/uploads";

        private Path ensureUploadsDir() throws IOException {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            return uploadDir;
        }


}
