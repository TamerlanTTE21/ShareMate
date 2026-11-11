package com.sharemate.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDetailsDto getItem(Long itemId,  Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    List<ItemWithBookingsDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, CommentCreateDto commentCreateDto);
}
