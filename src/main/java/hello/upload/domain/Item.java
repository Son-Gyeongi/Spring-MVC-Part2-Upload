package hello.upload.domain;

import lombok.Data;

import java.util.List;

// 파일 업로드 - 예제로 구현하는 파일 업로드, 다운로드
@Data
public class Item { // 상품을 관리

    private Long id;
    private String itemName; // 상품 이름
    private UploadFile attachFile; // 첨부파일 하나
    private List<UploadFile> imageFiles; // 이미지 파일 여러개
}
