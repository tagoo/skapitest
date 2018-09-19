package com.sunrun.utils.helper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.*;

@Setter
@Getter
@ToString
public class ChatRoom implements Serializable{
    private static final long serialVersionUID = -6353283462958522399L;
    private String roomName;
    private String description;
    private String password;
    private String subject;
    private String naturalName;
    private int maxUsers;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private Date creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    private Date modificationDate;
    private boolean persistent;
    private boolean publicRoom;
    private boolean registrationEnabled;
    private boolean canAnyoneDiscoverJID;
    private boolean canOccupantsChangeSubject;
    private boolean canOccupantsInvite;
    private boolean canChangeNickname;
    private boolean logEnabled;
    private boolean loginRestrictedToNickname;
    private boolean membersOnly;
    private boolean moderated;

    private List<String> broadcastPresenceRoles;

    private List<String> owners;

    private List<String> admins;

    private List<String> members;

    private List<String> memberGroups;

    private List<String> outcasts;

    public ChatRoom() {
        setDefaultProp();
    }

    public void setDefaultProp(){
        this.persistent = true;//房间持久化
        this.maxUsers = 0; //不限制
        this.publicRoom = true;//True if the room is searchable and visible through service discovery
        this.registrationEnabled = true;//True if users are allowed to register with the room. By default, room registration is enabled.
        this.canAnyoneDiscoverJID = true;//True if every presence packet will include the JID of every occupant.
        this.canOccupantsChangeSubject =true;//True if participants are allowed to change the room’s subject.
        this.canOccupantsInvite = true;//True if occupants can invite other users to the room.
        this.canChangeNickname = true ;//True if room occupants are allowed to change their nicknames in the room. By default, occupants are allowed to change their nicknames.
        this.logEnabled =true;//True if room occupants are allowed to change their nicknames in the room. By default, occupants are allowed to change their nicknames.
        this.loginRestrictedToNickname =false;// True if registered users can only join the room using their registered nickname. By default, registered users can join the room using any nickname.
        this.membersOnly = true;//True if the room requires an invitation to enter. That is if the room is members-only.
        this.moderated = true;//True if the room in which only those with “voice” may send messages to all occupants.
        this.broadcastPresenceRoles = Arrays.asList("moderator","participant","visitor");//The list of roles of which presence will be broadcasted to the rest of the occupants.
    }
}
