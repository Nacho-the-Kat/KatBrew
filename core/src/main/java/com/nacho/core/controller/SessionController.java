package com.nacho.core.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller für das Navigieren der Webseite innerhalb einer Session.
 */
@Controller
public class SessionController {

    /**
     * Handelt die Navigation nach Root. Falls eine Session existiert wird der Nutzer auf die Startseite
     * geleitet. Andernfalls auf die Login Seite.
     *
     * @return Die Html Datei der entsprechenden Seite.
     */
    @RequestMapping("")
    public String handleRoot() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "pages/index.html";
        } else {
            return "session/login.html";
        }
    }

    /**
     * Prüft ob ein mitgeschickter zu einem Cookie eine Session existiert.
     *
     * @return 200: falls eine Session existiert.
     *         401: falls keine Session existiert.
     */
    @RequestMapping("isCookieValid")
    public ResponseEntity isCookieValid() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    /**
     * Handelt die Navigation auf die Startseite.
     *
     * @return Die Html Datei der Startseite.
     */
    @RequestMapping("{_:^(?!socket).*$}")
    public String handleIndex() {
        return "pages/index.html";
    }

    /**
     * Request Mapping für fehler beim Login.
     *
     * @return Die Html Datei die bei einem Login Fehler aufgerufen wird.
     */
    @RequestMapping("/login_error")
    public String handleLoginError() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "pages/index.html";
        } else {
            return "session/loginError.html";
        }
    }
}
