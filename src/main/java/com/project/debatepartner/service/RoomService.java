package com.project.debatepartner.service;

import com.project.debatepartner.model.Room;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(String topic, String creator) {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, topic, creator);
        rooms.put(roomId, room);
        return room;
    }

    public Room getRoom(String roomId) {
        if (roomId == null) return null;
        return rooms.get(roomId.trim());
    }

    public boolean roomExists(String roomId) {
        return roomId != null && rooms.containsKey(roomId.trim());
    }

    public boolean joinRoom(String roomId, String username) {
        Room room = getRoom(roomId);
        if (room == null) return false;
        return room.addPlayer(username);
    }

    public void removePlayer(String roomId, String username) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.removePlayer(username);
        }
    }
}
