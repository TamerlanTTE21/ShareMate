package com.sharemate.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto);

    List<ItemRequestDto> getUserRequests(Long userId);

    ItemRequestDto getRequest(Long requestId, Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, int from, int size);
}
