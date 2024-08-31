package com.example.manageruser.Repository;

import com.example.manageruser.Model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByemail(String email);

    User findByUsername(String username);


}
