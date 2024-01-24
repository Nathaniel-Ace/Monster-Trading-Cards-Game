package at.fhtw.sampleapp.persistence.repository;

import at.fhtw.sampleapp.model.User;

public interface UserRepository {

    User findByUsername(String username);

    void addUser(User user);

    void insertUsernameDeck(String username);

    void insertUsernameStats(String username);

    void editUser(String username, User user);
}
