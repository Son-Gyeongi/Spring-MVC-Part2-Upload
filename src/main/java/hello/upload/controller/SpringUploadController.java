package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

// 파일 업로드 - 스프링과 파일 업로드
@Slf4j
@Controller
@RequestMapping("/spring/")
public class SpringUploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file,
                           HttpServletRequest request) throws IOException {
        // MultipartFile를 받아서 쓰면 코드가 깔끔해진다.

        log.info("request = {}", request);
        log.info("itemName = {}", itemName);
        log.info("multipartFile = {}", file);

        // 파일에 값이 들어있으면
        // MultipartFile 주요 메서드 getOriginalFilename(), transferTo()
        if (!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename(); // file.getOriginalFilename() : 업로드 파일 명
            log.info("파일 저장 fullPath = {}", fullPath);
            file.transferTo(new File(fullPath)); // file.transferTo() : 파일 저장
        }

        return "upload-form";
    }
}
