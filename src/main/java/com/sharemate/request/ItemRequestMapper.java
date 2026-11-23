package com.sharemate.request;

import com.sharemate.item.ItemDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemRequestMapper {
    public ItemRequestDto convertToDto(ItemRequest request, List<ItemDto> items) {
        return new  ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), items);
    }
}
