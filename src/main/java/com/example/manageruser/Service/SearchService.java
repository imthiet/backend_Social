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

    public Page<User> listAll(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return searchRepository.search(keyword, pageable);
        } else {
            return searchRepository.findAll(pageable);
        }
    }
}
