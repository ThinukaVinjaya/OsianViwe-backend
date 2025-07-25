package com.thinuka.osianViwe_hotel.response;

import jakarta.persistence.Lob;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

@Data
@NoArgsConstructor
public class RoomResponse {
    private  Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photo;
    private List<BookingResponse>bookings;


         public RoomResponse(Long id, String roomType, BigDecimal roomPrice) {
           this.id = id;
           this.roomType = roomType;
           this.roomPrice = roomPrice;
    }


    public RoomResponse(Long id, String roomType, BigDecimal roomPrice, boolean isBooked, String photoBytes) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photoBytes;
    }
}
