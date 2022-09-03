package model;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class User {
    public String email;
    public String password;
    public String name;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandomFullRegInfo() {
        final String email = randomAlphabetic(12) + "@gmail.com";
        final String password = randomAlphabetic(12);
        final String name = randomAlphabetic(12);
        return new User(email, password, name);
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public static User getRandomEmail() {
        return new User().setEmail(randomAlphabetic(12) + "@gmail.com");
    }

    public static User getRandomPassword() {
        return new User().setPassword(randomAlphabetic(12));
    }

    public static User getRandomName() {
        return new User().setName(randomAlphabetic(12));
    }

    public static User getRandomPasswordAndEmail() {
        return new User().setPassword(randomAlphabetic(12)).setEmail(randomAlphabetic(12) + "@gmail.com");
    }

    public static User getRandomNameAndEmail() {
        return new User().setName((randomAlphabetic(12))).setEmail(randomAlphabetic(12) + "@gmail.com");
    }

    public static User getRandomPasswordAndName() {
        return new User().setPassword((randomAlphabetic(12))).setName((randomAlphabetic(12)));
    }
}
