package com.katbrew.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pojo mit, mit den Authentifizierungs Daten.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Der Benutzername des anzumeldenden Nutzers.
     */
    public String username;

    /**
     * Das Passwort des anzumeldenden Benutzers.
     */
    public String password;

}

