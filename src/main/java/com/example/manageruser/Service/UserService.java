package com.example.manageruser.Service;

import com.example.manageruser.Model.UserNotFoundException;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    public List<User> getAllUsers() {
        return (List<User>) repo.findAll();
    }

    public void save(User user) {
        repo.save(user);
    }
    public boolean emailExists(String email) {
        return repo.findByemail(email) != null;
    }
   public boolean usernameExists(String username) {
        return  repo.findByUsername(username) != null;
   }
   public User findById(int id) {
      return  repo.findById(id).orElse(null);

   }

   public User findByEmail(String email) {
        return repo.findByemail(email);
   }
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
    public User get(Integer id) throws UserNotFoundException {
        Optional<User> userById = repo.findById(id);
        if(userById.isPresent()) {
            return userById.get();
        }
        throw new UserNotFoundException("Could not found this user!");

    }
}
