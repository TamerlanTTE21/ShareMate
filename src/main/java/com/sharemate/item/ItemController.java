package com.sharemate.item;

import com.sharemate.util.Headers;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(Headers.HEADER_ID) Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDetailsDto getItem(@PathVariable Long itemId,
                                  @RequestHeader(Headers.HEADER_ID) Long userId) {
        return itemService.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader(Headers.HEADER_ID) Long userId) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping
    public List<ItemDetailsDto> getAllItems(@RequestHeader(Headers.HEADER_ID) Long userId) {
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @RequestHeader(Headers.HEADER_ID) Long userId,
                                 @RequestBody CommentCreateDto commentCreateDto) {
        return itemService.addComment(itemId, userId, commentCreateDto);
    }
}
