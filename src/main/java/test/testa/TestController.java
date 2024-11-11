package test.testa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final S3Uploader s3Uploader;

    @GetMapping("/test")
    public String test() {
        return "testa";
    }

    // 파일 업로드 API
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // MultipartFile을 File 객체로 변환
            File convertedFile = convertMultipartFileToFile(file);

            // S3에 업로드
            String fileUrl = s3Uploader.upload(convertedFile.getAbsolutePath());

            // 변환된 파일 삭제 (임시 파일)
            convertedFile.delete();

            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 업로드에 실패했습니다.");
        }
    }

    // MultipartFile을 File로 변환하는 유틸리티 메서드
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
