package pl.umk.mat.plas.model;

import java.util.Objects;

public class User {
    private String nickname;
    private String password;
    private boolean isActive;

    public User(){}

    public User(String nickname, String password, boolean isActive) {
        this.nickname = nickname;
        this.password = password;
        this.isActive = isActive;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isActive == user.isActive &&
                Objects.equals(nickname, user.nickname) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, password, isActive);
    }
}
