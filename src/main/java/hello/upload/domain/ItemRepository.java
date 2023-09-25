package hello.upload.domain;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

// 파일 업로드 - 예제로 구현하는 파일 업로드, 다운로드
@Repository
public class ItemRepository {

    private final Map<Long, Item> store = new HashMap<>();
    private long sequence = 0L; // id를 하나씩 올려준다.

    // 아이템 저장
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    // 아이템 찾기
    public Item findById(Long id) {
        return store.get(id);
    }
}
