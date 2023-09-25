package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
}
