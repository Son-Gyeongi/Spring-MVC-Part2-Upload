package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

// 파일 업로드 - 예제로 구현하는 파일 업로드, 다운로드
@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    // 우리가 만든거 의존관계 주입으로 넣어보자.
    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String newItem(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        // 실제 저장되는 로직을 만들어보자.

        // 서버에 저장
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        // 데이터베이스에 저장
        /**
         * 파일은 데이터베이스에 저장하는 게 아니라 스토리지에 저장한다.
         * aws를 쓰는 경우 s3에 저장한다.
         * 데이터베이스에 저장하는 건 파일의 경로만 저장한다. 실제 파일 자체를 저장하지 않는다.
         */
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";
    }
    // 이제 저장은 완료했고 저장된 걸 고객에게 보여주고 파일을 실제로 다운로드 하는 기능을 만들어보자

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id); // item 찾기
        model.addAttribute("item", item);
        return "item-view";
    }

    // 파일을 다운로드 받는 특별한 컨트롤러를 만들어야 한다.
    // 이미지 보여주기
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        // filename은 794db018-3f76-4e9b-ac78-77f4d753fefe.png 로 들어온다.
        // "file:C:/study/upload/uploadTestFile/794db018-3f76-4e9b-ac78-77f4d753fefe.png"
        // UrlResource가 위 경로를 보고 파일을 찾아온다.
        return new UrlResource("file:" + fileStore.getFullPath(filename));

        // 좀 더 보안에 신경 쓰려면 체크로직을 넣는 게 좋다.
    }

    // 첨부파일 다운로드
    @GetMapping("/attach/{itemId}") // itemId : 상품ID
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        // item을 접근할 수 있는 사용자만 다운로드 받게 하고 싶어서 itemId를 받아봤다.
        Item item = itemRepository.findById(itemId);
        String storeFileName = item.getAttachFile().getStoreFileName();
        String uploadFileName = item.getAttachFile().getUploadFileName();
        // 사용자가 다운로드 받을 때 사용자가 업로드한 파일명이 나오게 하기 위해서 uploadFileName이 필요

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        // 한글또는 특수문자가 깨지는 경우 / 브라우저마다 다를 수 있다.
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        // 읽기만 하는게 아니라 다운로드 받기 위해서 추가적인 헤더를 넣어줘야 한다.
        // 규약이다. 브라우저가 첨부파일이라는 걸 인식해서 다운로드 받는다.
//        String contentDisposition = "attachment; filename=\"" + uploadFileName + "\"";
        // 인코딩된 파일명을 넣어주자
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource); // header 없이 body만 넣으면 웹 브라우저에서 읽기만 가능
    }
}
