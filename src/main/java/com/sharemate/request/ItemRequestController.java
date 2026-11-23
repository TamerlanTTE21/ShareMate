package com.sharemate.request;

import com.sharemate.util.Headers;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(Headers.HEADER_ID) Long userId,
                                     @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.addRequest(userId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(Headers.HEADER_ID) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Long requestId,  @RequestHeader(Headers.HEADER_ID) Long userId) {
        return itemRequestService.getRequest(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(Headers.HEADER_ID) Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10")  int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }
}
