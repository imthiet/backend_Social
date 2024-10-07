package com.example.manageruser.Service;

import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<User> suggestFriends(User currentUser, Pageable pageable) {
        Page<User> suggestedFriends = searchRepository.findFriendsByAddress(currentUser.getAddress(), currentUser, pageable);
        System.out.println("Number of suggested friends: " + suggestedFriends.getTotalElements());
        System.out.println("Content of suggested friends: " + suggestedFriends.getContent());
        return suggestedFriends;
    }
}

