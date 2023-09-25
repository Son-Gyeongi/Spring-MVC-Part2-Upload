package hello.upload.domain;

import lombok.Data;

// 파일 업로드 - 예제로 구현하는 파일 업로드, 다운로드
@Data
public class UploadFile {

    private String uploadFileName; // 고객이 업로드한 파일명
    private String storeFileName; // 서버 내부에서 관리하는 파일명
    // 구분하는 이유 고객이 같은 이름의 파일을 업로드하면 서버 내에서는 덮어버리게 된다.
    // storeFileName는 uuid로 안 겹치게 만들거다.


    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
