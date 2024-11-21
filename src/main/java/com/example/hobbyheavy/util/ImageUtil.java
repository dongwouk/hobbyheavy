package com.example.hobbyheavy.util;

import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
@Slf4j
public class ImageUtil {

    // 이미지 파일 저장
    public String saveImage(MultipartFile image, String uploadsDir) {

        if (image.getName().equals("")) {
            log.error("이미지가 없습니다.");
            throw new CustomException(ExceptionCode.IMAGE_IS_EMPTY);
        }

        if (!Objects.equals(image.getContentType(), "image/png") && !Objects.equals(image.getContentType(), "image/jpeg")) {
            log.error("이미지 확장자가 아닙니다. - {}", image.getContentType());
            throw new CustomException(ExceptionCode.IMAGE_EXTENSION_MISMATCH);
        }

        try {
            // 파일 이름 생성
            String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
            // 실제 파일이 저장될 경로
            String filePath = uploadsDir + fileName;

            Path path = Paths.get(filePath); // Path 객체 생성
            Files.createDirectories(path.getParent()); // 디렉토리 생성
            Files.write(path, image.getBytes()); // 디렉토리에 파일 저장
            log.info("이미지 저장이 완료되었습니다. 파일 경로 : {}", fileName);
            return fileName;

        } catch (IOException e) {
            log.error("IOException 예외 발생: {}", e.getMessage());
            throw new CustomException(ExceptionCode.IMAGE_IO_EXCEPTION);
        }

    }

}
