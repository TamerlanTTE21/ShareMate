package com.sharemate.item;

import com.sharemate.booking.Booking;
import com.sharemate.booking.BookingRepository;
import com.sharemate.booking.BookingStatus;
import com.sharemate.exception.ItemNotFoundException;
import com.sharemate.exception.NotOwnerException;
import com.sharemate.exception.UserNotFoundException;
import com.sharemate.exception.ValidationException;
import com.sharemate.user.User;
import com.sharemate.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemMapper itemMapper, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository, CommentMapper commentMapper, UserRepository userRepository) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
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
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = new Item(itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.convertToDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
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
    public ItemDetailsDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));

        LocalDateTime lastdate = null;
        LocalDateTime nextdate = null;
        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking last = bookingRepository
                    .findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(itemId, now, BookingStatus.APPROVED);
            Booking next = bookingRepository
                    .findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(itemId, now, BookingStatus.APPROVED);
            lastdate = last == null ? null : last.getStart();
            nextdate = next == null ? null : next.getStart();
        }
        List<CommentDto> comments = new ArrayList<>();
        for (Comment comment : commentRepository.findByItem_Id(itemId)) {
            comments.add(commentMapper.convertToDto(comment));
        }
        return new ItemDetailsDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), lastdate, nextdate, comments);
    }

    @Override
    public List<ItemDetailsDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findByOwner_Id(userId);
        List<ItemDetailsDto> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Item item : items) {
            Booking last = bookingRepository
                    .findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(item.getId(), now, BookingStatus.APPROVED);
            Booking next = bookingRepository
                    .findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(item.getId(), now, BookingStatus.APPROVED);
            LocalDateTime lastDate = last == null ? null : last.getStart();
            LocalDateTime nextDate = next == null ? null : next.getStart();

            List<CommentDto> comments = new ArrayList<>();
            for (Comment comment : commentRepository.findByItem_Id(item.getId())) {
                comments.add(commentMapper.convertToDto(comment));
            }

            result.add(new ItemDetailsDto(
                    item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                    lastDate, nextDate, comments));
        }
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.search(text);
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            result.add(itemMapper.convertToDto(item));
        }
        return result;
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentCreateDto commentCreateDto) {
        if (commentCreateDto.getText() == null || commentCreateDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        boolean hasBooked = bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (!hasBooked) {
            throw new ValidationException("Отзыв можно оставить только после завершённой аренды");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " не найдена"));

        Comment comment = new Comment(commentCreateDto.getText(), item, author, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.convertToDto(savedComment);
    }
}


