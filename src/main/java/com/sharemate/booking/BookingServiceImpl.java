package com.sharemate.booking;

import com.sharemate.exception.*;
import com.sharemate.item.Item;
import com.sharemate.item.ItemRepository;
import com.sharemate.user.User;
import com.sharemate.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto addBooking(BookingCreateDto bookingCreateDto, Long userId) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + bookingCreateDto.getItemId() + " не найдена"));
        if (!item.isAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (bookingCreateDto.getStart() == null || bookingCreateDto.getEnd() == null) {
            throw new ValidationException("Даты начала и окончания обязательны");
        }
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }
        if (item.getOwner().equals(userId)) {
            throw new ItemNotFoundException("Владелец не может бронировать свою вещь");
        }
        Booking booking = new Booking(null, item, booker, bookingCreateDto.getStart(), bookingCreateDto.getEnd(), BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.convertToDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Параметр approved обязателен");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с id " + bookingId + " не найдено"));
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new NotOwnerException("Только владелец вещи может подтвердить бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.convertToDto(savedBooking);
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        return bookingMapper.convertToDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);
            case PAST -> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId, now);
            case CURRENT ->
                    bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now);
            case WAITING ->
                    bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new ValidationException("Неизвестный state");
        }
        List<BookingDto> results = new ArrayList<>();
        for (Booking booking : bookings) {
            results.add(bookingMapper.convertToDto(booking));
        }
        return results;
    }


    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.findByItem_OwnerOrderByStartDesc(userId);
            case PAST -> bookings =  bookingRepository.findByItem_OwnerAndEndIsBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookings =  bookingRepository.findByItem_OwnerAndStartIsAfterOrderByStartDesc(userId, now);
            case CURRENT -> bookings =  bookingRepository.findByItem_OwnerAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now);
            case WAITING -> bookings =  bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookings = bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new ValidationException("Неизвестный state");
        }
        List<BookingDto> results = new ArrayList<>();
        for (Booking booking : bookings) {
            results.add(bookingMapper.convertToDto(booking));
        }
        return results;
    }

}
