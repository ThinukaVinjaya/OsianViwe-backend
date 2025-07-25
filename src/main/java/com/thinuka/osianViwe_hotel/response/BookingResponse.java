package com.thinuka.osianViwe_hotel.response;

import com.thinuka.osianViwe_hotel.model.Room;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long Id;
    private LocalDate checkInDate;
    private  LocalDate checkOutDate;
    private String guestName;
    private  String guestEmail;
    private int NumOfAdults;
    private int NumOfChildren;
    private int totalNumOfGuest;
    private  String bookingConfirmationCode;
    private RoomResponse room;

    public BookingResponse(Long id, LocalDate checkInDate, LocalDate checkOutDate,
                           String bookingConfirmationCode) {
        Id = id;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}
