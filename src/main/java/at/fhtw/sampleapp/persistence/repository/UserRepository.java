package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.User;

public interface UserRepository {

    User findById(int id);

    User findByUsername(String username);

    //Collection<User> findAllUsers();

    void addUser(User user);

    void insertUsernameDeck(String username);

    void insertUsernameStats(String username);

    void editUser(String username, User user);
}
