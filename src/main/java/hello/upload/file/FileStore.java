package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 파일 업로드 - 예제로 구현하는 파일 업로드, 다운로드
// 파일 저장과 관련된 업무를 처리하는 별도의 객체를 만들어보자
// 멀티파트 파일을 서버에 저장하는 역할을 담당
@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir; // 해당 경로에 파일 저장한다.

    public String getFullPath(String filename) {
        // filename을 받아서 fullPath를 만들자
        return fileDir + filename;
    }

    // 여러개 파일 저장
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>(); // 생성되는 UploadFile 담아주는 리스트
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) { // multipartFile가 비어있지 않으면
//                UploadFile uploadFile = storeFile(multipartFile);
//                storeFileResult.add(uploadFile);
                // ->인라인
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    // 1개 파일 저장
    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        // 스프링이 제공하는 MultipartFile를 가지고 파일 저장한 후 UploadFile로 반환한다.
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();// 사용자가 업로드한 파일이름, 예시 image.png
        String storeFileName = createStoreFileName(originalFilename); // 서버에 저장하는 파일명
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }

    // 서버에 저장하는 파일명
    // 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 UUID 를 사용해서 충돌하지 않도록 한다.
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename); // originalFilename에서 확장자 가져오기
        String uuid = UUID.randomUUID().toString(); // 예시 "qwe-qwe-123-qwe-asdd"
        return uuid + "." + ext; // "qwe-qwe-123-qwe-asdd.png"
    }

    // originalFilename에서 확장자 가져오기
    /**
     * 확장자를 별도로 추출해서 서버 내부에서 관리하는 파일명에도 붙여준다. 예를 들어서
     * 고객이 a.png 라는 이름으로 업로드 하면 51041c62-86e4-4274-801d-614a7d994edb.png 와 같이
     * 저장한다.
     */
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf("."); // .을 기준으로 위치를 나타낼 수 있다.
        return originalFilename.substring(pos + 1); // 확장자 png를 뽑을 수 있다.
    }
}
