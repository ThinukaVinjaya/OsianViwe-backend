package com.thinuka.osianViwe_hotel.controller;

import com.thinuka.osianViwe_hotel.exception.InvalidBookingRequestException;
import com.thinuka.osianViwe_hotel.exception.ResourceNotFoundException;
import com.thinuka.osianViwe_hotel.model.BookedRoom;
import com.thinuka.osianViwe_hotel.model.Room;
import com.thinuka.osianViwe_hotel.response.BookingResponse;
import com.thinuka.osianViwe_hotel.response.RoomResponse;
import com.thinuka.osianViwe_hotel.service.IBookingService;
import com.thinuka.osianViwe_hotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final IBookingService bookingService;
    private final IRoomService roomService;

    @GetMapping("all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookedRoom> bookings =  bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoom booking : bookings){
            BookingResponse bookingResponse =  getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }



    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode( @PathVariable String confirmationCode){
        try {
            BookedRoom booking =  bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse =  getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

        }

    }
    /*@PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBookings(Long roomId,
                                          @RequestBody BookedRoom bookingRequest){
     try{
         String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
         return ResponseEntity.ok("Room booked successfully ! Your booking confirmation code is :" +confirmationCode);
     }catch (InvalidBookingRequestException e){
         return ResponseEntity.badRequest().body(e.getMessage());
     }
    }*/

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBookings(@PathVariable Long roomId,
                                          @RequestBody BookedRoom bookingRequest) {
        try {
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully! Your booking confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/booking/{bookingId}/delete")
     public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
     }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice());

        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),room);
    }
}

