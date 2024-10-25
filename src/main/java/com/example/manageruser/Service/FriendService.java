package com.example.manageruser.Service;

import com.example.manageruser.Model.FriendShip;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    public List<User> getFriends(String username) {
        return friendRepository.findFriendsByUsername(username);
    }

    public boolean existsBetweenUsers(User currentUser, User friendUser) {
        return friendRepository.existsBetweenUsers(currentUser, friendUser);
    }
    public FriendShip findByUsers(User sender, User receiver) {
        return friendRepository.findByUserAndFriend(sender, receiver);
    }

    public void save(FriendShip friendShip) {
        friendRepository.save(friendShip);
    }

    // Check if a friend request is pending
    public boolean isFriendPending(User currentUser, User friendUser) {
        return friendRepository.existsByUserAndFriend(currentUser, friendUser);
    }

    // Check if users are friends (friendship is accepted)
    public boolean isFriendAccepted(User currentUser, User friendUser) {
        return friendRepository.existsByUserAndFriendAndAccepted(currentUser, friendUser, true);
    }


}
