package dev.dynxmic.dynamicbedwars.game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameParty {

    private final List<UUID> members;

    public GameParty() {
        members = new ArrayList<>();
    }

    public void addMember(UUID member) {
        members.add(member);
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public List<UUID> getMembers() {
        return members;
    }

}
