package hello.upload.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

// 파일 업로드 - 서블릿과 파일 업로드2
@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    // application.properties에서 경로를 가져온다.
    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFileV1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request = {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName = {}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts = {}", parts); // http 메시지 구조
        for (Part part : parts) { // iter 명령어 : 가까운 컬렉션을 loop 돌린다.
            log.info("==== PART ====");
            log.info("name = {}", part.getName());
            Collection<String> headerNames = part.getHeaderNames(); // part도 header와 body로 구분이 된다.
            for (String headerName : headerNames) {
                log.info("header {}: {}", headerName, part.getHeader(headerName));
            }
            // 편의 메서드
            // content-disposition; filename
            log.info("submittedFileName = {}", part.getSubmittedFileName()); // part.getSubmittedFileName() : 클라이언트가 전달한 파일명
            log.info("size = {}", part.getSize()); // part body size

            // 데이터 읽기 - body에 있는 데이터 읽는다.
            InputStream inputStream = part.getInputStream(); // part.getInputStream(): Part의 전송 데이터를 읽을 수 있다.
            // 바이너리를 문자로 바꾸든 문자를 바이너리로 바꾸든 항상 characterSet 정의 해줘야 한다.(StandardCharsets.UTF_8)
            // body에서 읽은 걸 String으로 읽는다.
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            // 큰 용량의 파일을 업로드를 테스트 할 때는 로그가 너무 많이 남아서 다음 옵션을 끄는 것이 좋다
            log.info("body = {}", body);

            // 파일에 저장하기
            if (StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName(); // 경로 잡아주기
                log.info("파일 저장 fullPath = {}", fullPath);
                part.write(fullPath); // part.write(...): Part를 통해 전송된 데이터를 저장할 수 있다.
            }
        }

        return "upload-form";
    }
}
