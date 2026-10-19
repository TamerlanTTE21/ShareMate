package com.sharemate.item;

import com.sharemate.exception.ItemNotFoundException;
import com.sharemate.exception.NotOwnerException;
import com.sharemate.exception.ValidationException;
import com.sharemate.user.UserService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemMapper itemMapper, UserService userService, ItemRepository itemRepository) {
        this.itemMapper = itemMapper;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userService.getUser(userId);
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Доступность не может быть пустым");
        }
        Item item = new Item(null, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId, null);
        Item savedItem = itemRepository.save(item);
        return itemMapper.convertToDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!item.getOwner().equals(userId)) {
            throw new NotOwnerException("Нет владельца");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());

        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item savedItem = itemRepository.save(item);
        return itemMapper.convertToDto(savedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));

        return itemMapper.convertToDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findByOwner(userId);

        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(itemMapper.convertToDto(item));
        }
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> result = new ArrayList<>();

        List<Item> items = itemRepository.search(text);

        for (Item item : items) {
            if (item.isAvailable()) {
                result.add(itemMapper.convertToDto(item));
            }
        }
        return result;
    }
}


