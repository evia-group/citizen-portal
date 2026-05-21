package com.evia.portal.userportal.core.util;

import com.evia.portal.userportal.core.exception.DocumentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUtilComprehensiveTest {

    private Path tempDir;

    @InjectMocks
    private FileUtil fileUtil;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a unique temp directory under target/ (always writable in the build tree)
        Path base = Paths.get("target", "test-tmp-fileutil");
        Files.createDirectories(base);
        tempDir = base.resolve(UUID.randomUUID().toString());
        Files.createDirectories(tempDir);
        ReflectionTestUtils.setField(fileUtil, "rootDirectory", tempDir.toAbsolutePath().toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                });
        }
    }

    // -------------------------------------------------------------------------
    // getFileAsResource
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should return a resource when a file matching the code prefix exists in the directory")
    void getFileAsResource_WhenFileExists_ReturnsResource() throws IOException {
        String fileCode = "abc123";
        Path testFile = tempDir.resolve(fileCode + "-document.pdf");
        Files.write(testFile, "test content".getBytes());

        Resource resource = fileUtil.getFileAsResource(fileCode);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.getFilename()).startsWith(fileCode);
    }

    @Test
    @DisplayName("should throw DocumentNotFoundException when no file matches the given code prefix")
    void getFileAsResource_WhenNoMatchingFile_ThrowsDocumentNotFoundException() {
        String nonExistentCode = "nonexistent-code-xyz";

        assertThatThrownBy(() -> fileUtil.getFileAsResource(nonExistentCode))
            .isInstanceOf(DocumentNotFoundException.class);
    }

    @Test
    @DisplayName("should throw DocumentNotFoundException when the root directory does not exist")
    void getFileAsResource_WhenRootDirectoryMissing_ThrowsDocumentNotFoundException() {
        ReflectionTestUtils.setField(fileUtil, "rootDirectory", "/nonexistent/path/that/cannot/exist");

        assertThatThrownBy(() -> fileUtil.getFileAsResource("anycode"))
            .isInstanceOf(DocumentNotFoundException.class)
            .hasMessageContaining("No document was found under the given path");
    }

    @Test
    @DisplayName("should find a file when multiple files start with the same code prefix")
    void getFileAsResource_WhenMultipleMatchingFiles_ReturnsMatch() throws IOException {
        String fileCode = "shared-prefix";
        Path file1 = tempDir.resolve(fileCode + "-first.pdf");
        Path file2 = tempDir.resolve(fileCode + "-second.pdf");
        Files.write(file1, "content1".getBytes());
        Files.write(file2, "content2".getBytes());

        Resource resource = fileUtil.getFileAsResource(fileCode);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.getFilename()).startsWith(fileCode);
    }

    @Test
    @DisplayName("should return a readable resource so callers can read its bytes")
    void getFileAsResource_ReturnedResourceIsReadable() throws IOException {
        String fileCode = "readtest";
        String expectedContent = "file body";
        Path testFile = tempDir.resolve(fileCode + "-test.txt");
        Files.write(testFile, expectedContent.getBytes());

        Resource resource = fileUtil.getFileAsResource(fileCode);

        assertThat(resource.isReadable()).isTrue();
        try (InputStream is = resource.getInputStream()) {
            String actual = new String(is.readAllBytes());
            assertThat(actual).isEqualTo(expectedContent);
        }
    }

    // -------------------------------------------------------------------------
    // saveFile
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("should save file and return a non-null UUID file code when directory already exists")
    void saveFile_WhenDirectoryExists_ReturnFileCode() throws IOException {
        String fileName = "report.pdf";
        byte[] content = "binary content".getBytes();
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        String fileCode = fileUtil.saveFile(fileName, multipartFile);

        assertThat(fileCode).isNotNull().isNotEmpty();
        assertThat(fileCode).hasSize(36); // UUID canonical form: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
    }

    @Test
    @DisplayName("should create the root directory when it does not exist before saving")
    void saveFile_WhenDirectoryDoesNotExist_CreatesDirectoryAndSaves() throws IOException {
        Path nonExistentDir = tempDir.resolve("new-subdir");
        ReflectionTestUtils.setField(fileUtil, "rootDirectory", nonExistentDir.toAbsolutePath().toString());
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String fileCode = fileUtil.saveFile("file.txt", multipartFile);

        assertThat(Files.exists(nonExistentDir)).isTrue();
        assertThat(fileCode).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("should persist the file content on disk under rootDirectory after saving")
    void saveFile_FileExistsOnDiskAfterSave() throws IOException {
        String fileName = "invoice.pdf";
        byte[] content = "invoice data".getBytes();
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        String fileCode = fileUtil.saveFile(fileName, multipartFile);

        Path expectedFile = tempDir.resolve(fileCode + "-" + fileName);
        assertThat(Files.exists(expectedFile)).isTrue();
        assertThat(Files.readAllBytes(expectedFile)).isEqualTo(content);
    }

    @Test
    @DisplayName("should return unique file codes on successive saves of files with the same name")
    void saveFile_ReturnsUniqueCodePerSave() throws IOException {
        String fileName = "duplicate.pdf";
        when(multipartFile.getInputStream())
            .thenReturn(new ByteArrayInputStream("a".getBytes()))
            .thenReturn(new ByteArrayInputStream("b".getBytes()));

        String code1 = fileUtil.saveFile(fileName, multipartFile);
        String code2 = fileUtil.saveFile(fileName, multipartFile);

        assertThat(code1).isNotEqualTo(code2);
    }

    @Test
    @DisplayName("should throw DocumentNotFoundException when getInputStream throws IOException")
    void saveFile_WhenGetInputStreamThrowsIOException_ThrowsDocumentNotFoundException() throws IOException {
        String fileName = "broken.pdf";
        when(multipartFile.getInputStream()).thenThrow(new IOException("stream error"));

        assertThatThrownBy(() -> fileUtil.saveFile(fileName, multipartFile))
            .isInstanceOf(DocumentNotFoundException.class)
            .hasMessageContaining(fileName);
    }

    @Test
    @DisplayName("should throw DocumentNotFoundException wrapping unexpected runtime exceptions during save")
    void saveFile_WhenUnexpectedRuntimeException_ThrowsDocumentNotFoundException() throws IOException {
        String fileName = "unexpected.pdf";
        when(multipartFile.getInputStream()).thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> fileUtil.saveFile(fileName, multipartFile))
            .isInstanceOf(DocumentNotFoundException.class)
            .hasMessageContaining(fileName);
    }

    @Test
    @DisplayName("should include the file name in the DocumentNotFoundException message on IOException")
    void saveFile_ExceptionMessageContainsFileName() throws IOException {
        String fileName = "report-q4.xlsx";
        when(multipartFile.getInputStream()).thenThrow(new IOException("disk full"));

        assertThatThrownBy(() -> fileUtil.saveFile(fileName, multipartFile))
            .isInstanceOf(DocumentNotFoundException.class)
            .hasMessageContaining(fileName);
    }
}
