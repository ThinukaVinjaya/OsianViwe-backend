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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }







    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBookings(@PathVariable Long roomId,
                                          @RequestBody BookedRoom bookingRequest) {
        try {
            // Validate basic booking request input here if needed

            // Delegate to service — service will ensure room is set properly
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);

            return ResponseEntity.ok("Room booked successfully! Your booking confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while booking the room.");
        }
    }



    @DeleteMapping("/booking/{bookingId}/delete")
     public void cancelBooking(@PathVariable Long bookingId){
        bookingService.cancelBooking(bookingId);
     }



    private BookingResponse getBookingResponse(BookedRoom booking) {
        RoomResponse roomResponse;

        if (booking.getRoom() != null) {
            Long roomId = booking.getRoom().getId();

            Room theRoom = roomService.getRoomById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

            roomResponse = new RoomResponse(
                    theRoom.getId(),
                    theRoom.getRoomType(),
                    theRoom.getRoomPrice());
        } else {
            roomResponse = null; // or set a default room response, or throw an exception, based on your needs
        }

        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                roomResponse);
    }




}

