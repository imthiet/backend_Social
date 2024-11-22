package com.example.manageruser.Service;

import com.example.manageruser.Dto.UserDto;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.SearchRepository;
import com.example.manageruser.WskConfig.BlobUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    public Page<User> listAll(String keyword, Pageable pageable, User currentUser) {
        if (keyword != null && !keyword.trim().isEmpty()) {

             Page<User> listF =  searchRepository.search(keyword, currentUser, pageable);
            System.out.println("litsF:"+listF.getContent());
            return listF;

        } else {
            return searchRepository.findFriendsByAddress(currentUser.getAddress(),currentUser, pageable);


        }

    }
    public Page<UserDto> listAllSearch(String keyword, Pageable pageable, User currentUser) {
        Page<User> users;

        if (keyword != null && !keyword.trim().isEmpty()) {
            users = searchRepository.search(keyword, currentUser, pageable);
        } else {
            users = searchRepository.findFriendsByAddress(currentUser.getAddress(), currentUser, pageable);
        }

        // Chuyển đổi Page<User> thành Page<UserDto>
        return new PageImpl<>(
                users.getContent().stream()
                        .map(user -> new UserDto(
                                user.getUsername(),
                                user.getEmail(),

                                currentUser.getFriends().contains(user), // isFriend
                                user.isFriendPending(),                 // isFriendPending
                                BlobUtil.blobToBase64(user.getImage()),                    // image
                                user.isFriendRequestReceiver()
                        ))
                        .collect(Collectors.toList()),
                pageable,
                users.getTotalElements()
        );
    }

    public Page<User> suggestFriends(User currentUser, Pageable pageable) {
        Page<User> suggestedFriends = searchRepository.findFriendsByAddress(currentUser.getAddress(), currentUser, pageable);
        System.out.println("Number of suggested friends: " + suggestedFriends.getTotalElements());
        System.out.println("Content of suggested friends: " + suggestedFriends.getContent());
        return suggestedFriends;
    }
}

