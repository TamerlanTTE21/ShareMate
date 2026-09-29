package com.sharemate.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemServiceImpl implements ItemService {
    private ItemMapper itemMapper;
    private Map<Long, Item> items;
    private Long nextId = 1L;

    public ItemServiceImpl() {
        itemMapper = new ItemMapper();
        items = new HashMap<>();
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Доступность не может быть пустым");
        }
        Item item = new Item(nextId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), userId);
        items.put(item.getId(), item);
        nextId++;
        return itemMapper.convertToDto(item);
    }

    @Override
    public void updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = items.get(itemId);

        if (item == null) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена");
        }
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

    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена");
        }
        return itemMapper.convertToDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) {
                result.add(itemMapper.convertToDto(item));
            }
        }
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> result = new ArrayList<>();
        String lowerText = text.toLowerCase();

        for (Item item : items.values()) {
            boolean nameMatches = item.getName().toLowerCase().contains(lowerText);
            boolean descriptionMatches = item.getDescription().toLowerCase().contains(lowerText);

            if ((nameMatches || descriptionMatches) && item.isAvailable()) {
                result.add(itemMapper.convertToDto(item));
            }
        }
        return result;
    }
}


