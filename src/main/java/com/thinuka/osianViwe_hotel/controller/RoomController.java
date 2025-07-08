package com.thinuka.osianViwe_hotel.controller;

import com.thinuka.osianViwe_hotel.model.Room;
import com.thinuka.osianViwe_hotel.response.RoomResponse;
import com.thinuka.osianViwe_hotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin("http://localhost:5174")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {


    private  final IRoomService roomService;

   /* @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5174")
                    .allowedMethods("GET", "POST", "PUT", "DELETE");

        }
    }*/

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType")String roomType,
            @RequestParam("roomPrice")BigDecimal roomPrice) throws SQLException, IOException {
        Room savedRoom =  roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response =  new RoomResponse(savedRoom.getId(),
                savedRoom.getRoomType(), savedRoom.getRoomPrice());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }
}
