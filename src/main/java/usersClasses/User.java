package usersClasses;

import java.io.Serializable;

public abstract class User implements Serializable {

    private String surname;
    private String name;
    private String secondName;
    private String userName;
    private char[] password;

    public User(String secondName, String surname, String name) {
        this.secondName = secondName;
        this.surname = surname;
        this.name = name;
        userName = surname + " " + name + " " + secondName;
    }

    public User(String surname, String name, String secondName, char[] password) {
        this.surname = surname;
        this.name = name;
        this.secondName = secondName;
        this.password = password;
        userName = surname + " " + name + " " + secondName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    @Override
    public String toString() {
        return userName;
    }
}
