package com.katbrew.core.security;

import com.katbrew.entities.jooq.db.Tables;
import com.katbrew.entities.jooq.db.tables.pojos.Users;
import com.katbrew.services.tables.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Auth Manager for Username Password auth.
 */
@Configuration
@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationManager implements AuthenticationManager {

    private final UsersService usersService;

    /**
     * Checks if the loggin data matches the user data.
     *
     * @param authentication the basic auth data.
     * @return UsernamePasswordAuthenticationToken, with the result.
     * @throws AuthenticationException if the user does not exist.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getPrincipal().toString();
        final String password = authentication.getCredentials().toString();
        try {
            String encrypted = new String(Hex.encode(MessageDigest.getInstance("SHA512")
                    .digest(password.getBytes(StandardCharsets.UTF_8))));

            final List<Users> user = usersService.findBy(List.of(
                    Tables.USERS.USERNAME.eq(username),
                    Tables.USERS.PASSWORD.eq(encrypted)
            ));

            if (!user.isEmpty()) {
                return new UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        new ArrayList<>()
                );
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        throw new BadCredentialsException("User with given credentials doesn't exist");
    }
}
