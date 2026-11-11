package com.sharemate.booking;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, BookingState state);

    List<BookingDto> getOwnerBookings(Long userId, BookingState state);}
