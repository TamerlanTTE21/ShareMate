package com.sharemate.item;

public class ItemMapper {
    public ItemDto convertToDto(Item item) {
        return new ItemDto(item.getName(), item.getDescription(), item.isAvailable());
    }
}
