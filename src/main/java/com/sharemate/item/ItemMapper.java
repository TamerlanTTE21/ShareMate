package com.sharemate.item;

import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public ItemDto convertToDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }
}
