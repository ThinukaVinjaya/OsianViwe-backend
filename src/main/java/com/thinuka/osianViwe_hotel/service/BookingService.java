package com.thinuka.osianViwe_hotel.service;


import com.thinuka.osianViwe_hotel.exception.InvalidBookingRequestException;
import com.thinuka.osianViwe_hotel.exception.ResourceNotFoundException;
import com.thinuka.osianViwe_hotel.model.BookedRoom;
import com.thinuka.osianViwe_hotel.model.Room;
import com.thinuka.osianViwe_hotel.repository.BookedRoomRepository;
import com.thinuka.osianViwe_hotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService implements  IBookingService{

    private final BookingRepository bookingRepository;
    private final IRoomService roomService;

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }


    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {

        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public void cancelBooking(Long bookingId) {
       bookingRepository.deleteById(bookingId);
    }

    /*@Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingbookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingbookings);
        if(roomIsAvailable){
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        }else{
            throw new InvalidBookingRequestException("Sorry,This room is not available for the selected dates;");
        }
        return bookingRequest.getBookingConfirmationCode();
    }*/

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        // Validate date logic
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date.");
        }

        // Safely get the Room
        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        // Check availability
        List<BookedRoom> existingBookings = room.getBookings();
        boolean isAvailable = roomIsAvailable(bookingRequest, existingBookings);

        if (!isAvailable) {
            throw new InvalidBookingRequestException("Sorry, this room is not available for the selected dates.");
        }

        // Set room to bookingRequest to avoid NullPointerException
        bookingRequest.setRoom(room);

        // Generate and set unique booking confirmation code
        String confirmationCode = UUID.randomUUID().toString();
        bookingRequest.setBookingConfirmationCode(confirmationCode);

        // Associate the booking with the room (if needed)
        room.addBooking(bookingRequest); // optional if cascade is set up in JPA

        // Save the booking
        bookingRepository.save(bookingRequest);

        return confirmationCode;
    }


   /* @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-out date must come after check-in date");
        }

        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new InvalidBookingRequestException("Room not found"));

        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);

        if (!roomIsAvailable) {
            throw new InvalidBookingRequestException("Sorry, this room is not available for the selected dates.");
        }

        // ✅ Create a new BookedRoom and populate all fields
        BookedRoom booking = new BookedRoom();
        booking.setGuestFullName(bookingRequest.getGuestFullName());
        booking.setGuestEmail(bookingRequest.getGuestEmail());
        booking.setCheckInDate(bookingRequest.getCheckInDate());
        booking.setCheckOutDate(bookingRequest.getCheckOutDate());
        booking.setNumOfAdults(bookingRequest.getNumOfAdults()); // ✅ This is the missing part
        booking.setNumOfChildren(bookingRequest.getNumOfChildren()); // ✅ This too


        // Associate room
        booking.setRoom(room);
        room.addBooking(booking);

        bookingRepository.save(booking);

        return booking.getBookingConfirmationCode();
    }*/




    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode);
    }



    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingbookings) {
        return existingbookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}
