package de.sep.javafx.services;

import de.sep.domain.dto.LoggedUserDto;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthenticationServiceTest {

    AuthenticationService authenticationService;
    UserService userService;

    @Before
    public void setUp() {
        HttpClient httpClient = new HttpClient();
        authenticationService = new AuthenticationService(httpClient);
        userService = new UserService(authenticationService, httpClient);
    }

    @Test
    public void registerTest() throws Exception {

        int userid = authenticationService.register("RegistrierungsTest", "registrierung", "reg@reg.de", "Oskarstraße 1", "46149", "Oberhausen", 0, "", "", 0).getData();

        LoggedUserDto currentUser = authenticationService.login("RegistrierungsTest", "registrierung").getData();

        assertEquals(userid, currentUser.getUserid());

    }



}