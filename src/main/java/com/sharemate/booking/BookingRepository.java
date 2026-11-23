package com.sharemate.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Booking findFirstByItem_IdAndStartIsBeforeAndStatusOrderByStartDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Booking findFirstByItem_IdAndStartIsAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);

    boolean existsByItem_IdAndBooker_IdAndStatusAndEndIsBefore(Long itemId, Long bookerId, BookingStatus status,  LocalDateTime now);
}
