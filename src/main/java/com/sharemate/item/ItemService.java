package com.sharemate.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto getItem(Long itemId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    List<ItemDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);
}
