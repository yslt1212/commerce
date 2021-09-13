package de.sep.server.controller;

import de.sep.server.adapter.UserAdapter;
import de.sep.domain.dto.GetUserDto;
import de.sep.domain.dto.UpdateUserDto;
import de.sep.domain.entity.UserEntity;
import de.sep.server.controller.UsersController;
import de.sep.server.errors.BadRequestException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UsersControllerTest {

   UsersController usersController;

    @Rule
    public final ExpectedException unauthorizedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        usersController = spy(new UsersController());

    }

    @Test
    public void doGetTest() throws UnauthorizedException, SQLException, NotFoundException, InternalServerErrorException, IOException {
        doNothing().when(usersController).authenticate(any());
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userid")).thenReturn(100);
        UserAdapter userAdapter = mock(UserAdapter.class);
        UserEntity mockedUser = new UserEntity(100, "Mocked User", "pw", "mocked@email.de", "Oskarstraße 3","46149", "Oberhausen", 2.0, 0, "", 1);
        GetUserDto acceptedDto = new GetUserDto(mockedUser.getId(), mockedUser.getUsername(), mockedUser.getEmail(), mockedUser.getStreet(),mockedUser.getPostcode(), mockedUser.getCity(), mockedUser.getBalance(), mockedUser.getType(), mockedUser.getImg(), mockedUser.getWantsTFA());
        when(userAdapter.getUserById(anyInt())).thenReturn(mockedUser);
        usersController.userAdapter = userAdapter;

        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);

        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(resp.getWriter()).thenReturn(out);


        usersController.doGet(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<GetUserDto>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(usersController).sendResponse(captorUseles.capture(), captor.capture());


        final ResponseEntity<GetUserDto> returnedResponseEnity = captor.getValue();

        GetUserDto getUserDto = returnedResponseEnity.getData();

        assertEquals(acceptedDto, getUserDto);
    }

    @Test
    public void doGetUnauthorizedExceptionTest() throws UnauthorizedException, SQLException, NotFoundException, InternalServerErrorException, IOException {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        int acceptedStatus = HttpStatus.UNAUTHORIZED_401;
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);
        UserAdapter userAdapter = mock(UserAdapter.class);
        when(userAdapter.getUserById(anyInt())).thenThrow(NotFoundException.class);

        usersController.doGet(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<GetUserDto>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(usersController).sendResponse(captorUseles.capture(), captor.capture());


        final ResponseEntity<GetUserDto> returnedResponseEnity = captor.getValue();
        GetUserDto getUserDto = returnedResponseEnity.getData();
        int returnedStatus = returnedResponseEnity.getStatus();

        assertEquals(acceptedStatus, returnedStatus);
        assertEquals(null, getUserDto);

    }


    @Test
    public void doGetNotFoundExceptionTest() throws IOException, UnauthorizedException, NotFoundException, InternalServerErrorException {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("userid")).thenReturn(100);
        doNothing().when(usersController).authenticate(any());
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);

        HttpServletResponse resp = mock(HttpServletResponse.class);
        int acceptedStatus = HttpStatus.NOT_FOUND_404;
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);

        UserAdapter userAdapter = mock(UserAdapter.class);

        when(userAdapter.getUserById(anyInt())).thenThrow(NotFoundException.class);

        usersController.doGet(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<GetUserDto>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(usersController).sendResponse(captorUseles.capture(), captor.capture());


        final ResponseEntity<GetUserDto> returnedResponseEnity = captor.getValue();
        GetUserDto getUserDto = returnedResponseEnity.getData();
        int returnedStatus = returnedResponseEnity.getStatus();

        assertEquals(acceptedStatus, returnedStatus);
        assertEquals(null, getUserDto);
    }

    @Test
    public void doPutTest() throws UnauthorizedException, BadRequestException, InternalServerErrorException, NotFoundException, IOException {
        // Mocking httpSession
        HttpSession session = mock(HttpSession.class);
        // Mocking the authentication to be successfull
        doNothing().when(usersController).authenticate(any(), anyInt());

        // Mocking the useradapter and his function so we dont need to use db
        UserAdapter userAdapter = mock(UserAdapter.class);
        UserEntity mockedUser = new UserEntity(100, "Mocked User", "pw", "mocked@email.de", "Oskarstraße 3","46149", "Oberhausen", 2.0, 0, "", 1);
        doNothing().when(userAdapter).updateUser(anyInt(), any());
        UpdateUserDto acceptedDto = new UpdateUserDto(mockedUser.getUsername(), mockedUser.getEmail(), mockedUser.getStreet(),mockedUser.getPostcode(), mockedUser.getCity(), mockedUser.getImg(), mockedUser.getWantsTFA());
        when(userAdapter.getUserById(anyInt())).thenReturn(mockedUser);
        usersController.userAdapter = userAdapter;

        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);

        BufferedReader mockedBrRead = mock(BufferedReader.class);
        when(req.getReader()).thenReturn(mockedBrRead);

        // Mocking the params out of the query
        Map<String, String> params = new HashMap<>();
        params.put("userid", "100");

        doReturn(params).when(usersController).getQueryParameters(any());

        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(resp.getWriter()).thenReturn(out);

        usersController.doPut(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<UpdateUserDto>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(usersController).sendResponse(captorUseles.capture(), captor.capture());


        final ResponseEntity<UpdateUserDto> returnedResponseEnity = captor.getValue();
        UpdateUserDto updateUserDto = returnedResponseEnity.getData();

        assertEquals(acceptedDto, updateUserDto);
    }

    @Test
    public void doPutTestUnauthorized() throws UnauthorizedException, BadRequestException, InternalServerErrorException, NotFoundException, IOException {
        // Mocking httpSession
        HttpSession session = mock(HttpSession.class);
        int acceptedStatus = HttpStatus.UNAUTHORIZED_401;

        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);

        // Mocking the params out of the query
        Map<String, String> params = new HashMap<>();
        params.put("userid", "100");

        doReturn(params).when(usersController).getQueryParameters(any());

        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);

        BufferedReader mockedBrRead = mock(BufferedReader.class);
        when(req.getReader()).thenReturn(mockedBrRead);

        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(resp.getWriter()).thenReturn(out);

        usersController.doPut(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<UpdateUserDto>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(usersController).sendResponse(captorUseles.capture(), captor.capture());


        final ResponseEntity<UpdateUserDto> returnedResponseEnity = captor.getValue();

        assertEquals(acceptedStatus, returnedResponseEnity.getStatus());
        assertEquals(null, returnedResponseEnity.getData());
    }

    @Test
    public void doPutTestNotFoundException() throws UnauthorizedException, BadRequestException, InternalServerErrorException, NotFoundException, IOException {
        // Mocking httpSession
        HttpSession session = mock(HttpSession.class);
        // Mocking the authentication to be successfull
        doNothing().when(usersController).authenticate(any(), anyInt());
        int acceptedStatus = HttpStatus.NOT_FOUND_404;

        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);

        // Mocking the params out of the query
        Map<String, String> params = new HashMap<>();
        params.put("userid", "100");

        UserAdapter userAdapter = mock(UserAdapter.class);
        doNothing().when(userAdapter).updateUser(anyInt(), any());
        when(userAdapter.getUserById(anyInt())).thenThrow(NotFoundException.class);

        usersController.userAdapter = userAdapter;

        doReturn(params).when(usersController).getQueryParameters(any());



        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(session);


        BufferedReader mockedBrRead = mock(BufferedReader.class);
        when(req.getReader()).thenReturn(mockedBrRead);

        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(resp.getWriter()).thenReturn(out);

        usersController.doPut(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<UpdateUserDto>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(usersController).sendResponse(captorUseles.capture(), captor.capture());


        final ResponseEntity<UpdateUserDto> returnedResponseEnity = captor.getValue();

        assertEquals(acceptedStatus, returnedResponseEnity.getStatus());
        assertEquals(null, returnedResponseEnity.getData());
    }

}