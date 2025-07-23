package com.thinuka.osianViwe_hotel.service;



import com.thinuka.osianViwe_hotel.model.User;

import java.util.List;

public interface IUserService {

    User registerUser(User user);

    List<User> getUsers();

    void deleteUser(String email);

    User getUser(String email);
}
