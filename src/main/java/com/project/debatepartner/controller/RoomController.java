package com.project.debatepartner.controller;

import com.project.debatepartner.model.Room;
import com.project.debatepartner.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "General Debate");
        String username = request.getOrDefault("username", "Debater");

        Room room = roomService.createRoom(topic, username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("roomId", room.getRoomId());
        response.put("topic", room.getTopic());
        response.put("creator", room.getCreator());
        response.put("players", room.getPlayers());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomInfo(@PathVariable String roomId) {
        Room room = roomService.getRoom(roomId);
        Map<String, Object> response = new HashMap<>();

        if (room == null) {
            response.put("exists", false);
            response.put("message", "Room not found");
            return ResponseEntity.status(404).body(response);
        }

        response.put("exists", true);
        response.put("roomId", room.getRoomId());
        response.put("topic", room.getTopic());
        response.put("creator", room.getCreator());
        response.put("players", room.getPlayers());
        response.put("playerCount", room.getPlayers().size());
        response.put("ended", room.isEnded());

        return ResponseEntity.ok(response);
    }
}
