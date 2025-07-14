package com.thinuka.osianViwe_hotel.repository;

import com.thinuka.osianViwe_hotel.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {

     BookedRoom findByBookingConfirmationCode(String confirmationCode);

    public List<BookedRoom> findByRoomId(Long roomId);

}
