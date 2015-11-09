package usersClasses;

import java.util.Set;

public interface UserDAO<T> {

    Set<T> getAllUsers();

    T getUser(int index);

    void updateUser(T user);

    void deleteUser(T user);

    void addUser(T user);
}
