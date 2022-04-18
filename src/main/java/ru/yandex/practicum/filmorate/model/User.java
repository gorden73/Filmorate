package model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    @NonNull
    private final int userId;
    private final String email;
    private final String login;
    private final String nickName;
    private final LocalDate birthDate;
}
