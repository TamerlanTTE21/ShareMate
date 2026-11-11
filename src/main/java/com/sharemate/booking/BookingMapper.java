package com.sharemate.booking;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingDto convertToDto(Booking booking) {
        BookingItemDto itemDto = new BookingItemDto(booking.getItem().getId(), booking.getItem().getName());
        BookingUserDto bookerDto = new BookingUserDto(booking.getBooker().getId());
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), itemDto, bookerDto, booking.getStatus());
    }
}
