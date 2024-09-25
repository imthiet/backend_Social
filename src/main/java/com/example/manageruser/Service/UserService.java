package com.example.manageruser.Service;

import com.example.manageruser.Model.UserDto;
import com.example.manageruser.Model.UserNotFoundException;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import com.example.manageruser.WskConfig.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.repo = userRepository;
    }
//    public User findByUsername(String username) {
//        return repo.findByUsername(username);
//    }
    public List<User> getAllUsers() {

        return (List<User>) repo.findAll();
    }

    public void save(User user) {

        repo.save(user);
    }

    public boolean emailExists(String email) {
        return repo.findByEmail(email) != null;
    }

    public boolean usernameExists(String username) {
        return repo.findByUsername(username).isPresent(); // Cập nhật
    }
   public User findById(int id) {
      return  repo.findById(id).orElse(null);

   }

//   public User findByEmail(String email) {
//        return repo.findByEmail(email);
//   }



public User findByEmail(String email) {
    return repo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
}

    public User findByUsername(String username) {
    // Xử lý Optional<User> bằng orElseThrow() để ném ngoại lệ nếu không tìm thấy
    return repo.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
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

    public boolean verify(String verificationCode) {
        User user = repo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setEnabled(true);
            user.setVerificationCode(null);
            repo.save(user);
            return true;
        }
    }
    public void registerUser(UserDto userDto) {
        // Kiểm tra username
        if (repo.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Người dùng đã tồn tại");
        }

        // Kiểm tra email
        if (repo.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email đã tồn tại");
        }

        // Tạo người dùng mới
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setEmail(userDto.getEmail());
        // Các thuộc tính khác...
        repo.save(newUser);
    }


}
