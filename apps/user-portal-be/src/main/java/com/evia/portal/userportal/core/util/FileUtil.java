package com.evia.portal.userportal.core.util;

import com.evia.portal.userportal.core.exception.DocumentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileUtil {

  @Value("${file-storage.root-directory}")
  String rootDirectory;

  public Resource getFileAsResource(String fileCode) {
    try {
      Path dirPath = Paths.get(rootDirectory);

      try (var stream = Files.list(dirPath)) {
        Optional<Path> foundFile = stream
          .filter(file -> file.getFileName().toString().startsWith(fileCode))
          .findFirst();

        return foundFile.map(file -> {
          try {
            return new UrlResource(file.toUri());
          } catch (IOException e) {
            throw new DocumentNotFoundException("");
          }
        }).orElseThrow(() -> new DocumentNotFoundException(""));
      }
    } catch (IOException e) {
      throw new DocumentNotFoundException("No document was found under the given path");
    }
  }

  public String saveFile(String fileName, MultipartFile multipartFile) {
    try {
      Path uploadPath = Paths.get(rootDirectory);

      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      String fileCode = UUID.randomUUID().toString();

      try (InputStream inputStream = multipartFile.getInputStream()) {
        Path filePath = uploadPath.resolve(fileCode + "-" + fileName);
        Files.copy(inputStream, filePath);
      }

      return fileCode;
    } catch (IOException e) {
      throw new DocumentNotFoundException("Could not save file: " + fileName);
    } catch (Exception e) {
      // Catch any other unexpected exceptions
      throw new DocumentNotFoundException("An unexpected error occurred while saving file: " + fileName);
    }
  }
}
