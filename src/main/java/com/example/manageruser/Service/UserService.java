package com.example.manageruser.Service;

import com.example.manageruser.Dto.UserDto;
import com.example.manageruser.Dto.UserManageDto;
import com.example.manageruser.Model.UserNotFoundException;
import com.example.manageruser.Model.User;
import com.example.manageruser.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.repo = userRepository;
    }

    //    public User findByUsername(String username) {
//        return repo.findByUsername(username);
//    }
    public List<UserDto> getAllUsers() {
        List<User> users = (List<User>) repo.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


//    public void save(User user) {
//
//        repo.save(user);
//    }
// Phương thức chuyển đổi từ User sang UserManageDTO
 public UserManageDto convertToUserManageDTO(User user) {
    return new UserManageDto(
            user.getId(),
            user.getUsername(),
            user.getVerificationCode(),
            user.isEnabled(),
            user.getEmail(),
            user.isAdmin()
    );
}

    public boolean emailExists(String email) {
        return repo.findByEmail(email) != null;
    }

    public boolean usernameExists(String username) {
        return repo.findByUsername(username).isPresent(); // Cập nhật
    }

    public User findById(long id) {
        return repo.findById(id).orElse(null);

    }
    public User addUser(User user){
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
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
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }



    public void deleteById(long id) {
        repo.deleteById(id);
    }

    public User get(long id) throws UserNotFoundException {
        Optional<User> userById = repo.findById(id);
        if (userById.isPresent()) {
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

    public void save(User user) {
        // Kiểm tra username đã tồn tại
        if (repo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }

        // Kiểm tra email đã tồn tại
        if (repo.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        repo.save(user);
    }

    public void saveAgaint(User user) {
        repo.save(user);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        // Convert BLOB to Base64 string
        if (user.getImage() != null) {
            try {
                Blob imageBlob = user.getImage();
                byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                dto.setImage("data:image/jpeg;base64," + base64Image); // Ensure the correct prefix
                System.out.println("Successfully converted image to Base64 for user: " + user.getUsername());
            } catch (SQLException e) {
                System.err.println("Error retrieving image for user: " + user.getUsername());
                e.printStackTrace();
            }
        } else {
            dto.setImage(null); // Handle null image case
            System.out.println("No image found for user: " + user.getUsername());
        }

        return dto;
    }


    public String getUsernameById(long id) {
       return repo.findById(id).get().getUsername();
    }
}
