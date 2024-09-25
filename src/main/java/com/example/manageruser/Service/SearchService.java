package com.example.manageruser.Service;

import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    private SearchRepository searchRepository;

    public List<User> listAll(String keyword)
    {
        if(keyword != null )
        {
            return searchRepository.search(keyword);
        }
        else {
            return (List<User>)searchRepository.findAll();

        }
    }
}
