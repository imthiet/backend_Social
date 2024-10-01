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

    public void save(FriendShip friendShip) {
        friendRepository.save(friendShip);
    }
    // Kiểm tra xem đã gửi yêu cầu kết bạn hay chưa
    public boolean isFriendPending(User currentUser, User friendUser) {
        return friendRepository.existsByUserAndFriend(currentUser, friendUser);
    }
}
