package com.sunrun.utils.helper;

import java.util.List;

public class RoomData {
    private List<ChatRoom> chatRooms;

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    @Override
    public String toString() {
        return "RoomData{" +
                "chatRooms=" + chatRooms +
                '}';
    }
}
