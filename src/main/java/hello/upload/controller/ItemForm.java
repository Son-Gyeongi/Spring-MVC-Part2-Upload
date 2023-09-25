package hello.upload.controller;

// 파일 업로드 - 예제로 구현하는 파일 업로드, 다운로드

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 상품을 폼을 가지고 데이터가 왔다갔다 해야한다.
 * 데이터 전달하는 폼을 만들어보자.
 * 상품 저장용 폼이다.
 */
@Data
public class ItemForm {

    private Long itemId;
    private String itemName;
    private MultipartFile attachFile; // 멀티파트는 @ModelAttribute 에서 사용할 수 있다.
    private List<MultipartFile> imageFiles; // 이미지를 다중 업로드 하기 위해 MultipartFile 를 사용했다
}
