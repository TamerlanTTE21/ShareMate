package com.sharemate.request;

import com.sharemate.exception.ItemRequestNotFoundException;
import com.sharemate.exception.UserNotFoundException;
import com.sharemate.exception.ValidationException;
import com.sharemate.item.Item;
import com.sharemate.item.ItemDto;
import com.sharemate.item.ItemMapper;
import com.sharemate.item.ItemRepository;
import com.sharemate.user.User;
import com.sharemate.user.UserRepository;
import com.sharemate.util.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository, ItemMapper itemMapper, ItemRequestMapper itemRequestMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.itemRequestMapper = itemRequestMapper;
    }

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        if (itemRequestCreateDto.getDescription() == null || itemRequestCreateDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }

        ItemRequest request = new ItemRequest(itemRequestCreateDto.getDescription(), requestor, LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(request);
        return itemRequestMapper.convertToDto(savedRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest request : requests) {
            result.add(toDto(request));
        }
        return result;
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос с id " + requestId + " не найден"));

        return toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        Pageable pageable = Pagination.of(from, size);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest request : itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId, pageable)) {
            result.add(toDto(request));
        }
        return result;
    }

    private ItemRequestDto toDto(ItemRequest request) {
        List<ItemDto> items = new ArrayList<>();
        for (Item item : itemRepository.findByRequest(request.getId())) {
            items.add(itemMapper.convertToDto(item));
        }
        return itemRequestMapper.convertToDto(request, items);
    }
}
