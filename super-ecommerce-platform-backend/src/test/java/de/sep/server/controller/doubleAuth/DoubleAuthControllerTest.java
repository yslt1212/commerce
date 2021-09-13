package de.sep.server.controller.doubleAuth;

import de.sep.domain.dto.GetUserDto;
import de.sep.server.adapter.DoubleAuthAdapter;
import de.sep.server.adapter.EmailAdapter;
import de.sep.server.controller.doubleAuth.DoubleAuthController;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.errors.UnauthorizedException;
import de.sep.server.util.ResponseEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoubleAuthControllerTest {

    DoubleAuthController doubleAuthController;
    EmailAdapter emailAdapter;
    @Before
    public void setUp() throws Exception {
        doubleAuthController = spy(DoubleAuthController.class);
        emailAdapter = mock(EmailAdapter.class);
        doubleAuthController.emailAdapter = emailAdapter;
    }

    @Test
    public void doPostTest() throws UnauthorizedException, ServletException, IOException, NotFoundException, SQLException {

        Map<String, String> map = new HashMap<>();
        map.put("email", "yannick.dohmen@test.de");
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);

        // Creates code
        doubleAuthController.doPost(req, resp);

        final ArgumentCaptor<String> captorSubject = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> captorContent = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> captorEmail = ArgumentCaptor.forClass(String.class);

        verify(emailAdapter).sendMail(captorSubject.capture(), captorContent.capture(), captorEmail.capture());


        final String content = captorContent.getValue();

        String code = content.substring(content.length() -5, content.length());

        Assert.assertFalse(code == null);
        Assert.assertEquals(captorEmail.getValue(), "yannick.dohmen@test.de");

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());

        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertEquals(200, returnedResponseEnity.getStatus());
    }

    @Test
    public void doPostNotFoundExceptionTest() throws UnauthorizedException, ServletException, IOException, NotFoundException, SQLException {

        Map<String, String> map = new HashMap<>();
        map.put("email", "yannick.dohmen@test.de");
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        DoubleAuthAdapter doubleAuthAdapter = mock(DoubleAuthAdapter.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);
        when(doubleAuthAdapter.getCodeById(anyInt())).thenThrow(NotFoundException.class);

        doubleAuthController.doubleAuthAdapter = doubleAuthAdapter;

        // Creates code
        doubleAuthController.doPost(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());



        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertEquals(404,returnedResponseEnity.getStatus());
    }

    @Test
    public void doPostBadRequestExceptionTest() throws UnauthorizedException, ServletException, IOException, NotFoundException, SQLException {

        Map<String, String> map = new HashMap<>();
        map.put("email", null);
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        DoubleAuthAdapter doubleAuthAdapter = mock(DoubleAuthAdapter.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);
        doubleAuthController.doubleAuthAdapter = doubleAuthAdapter;

        // Creates code
        doubleAuthController.doPost(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());



        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertEquals(400,returnedResponseEnity.getStatus());
    }

    @Test
    public void doPostInternalServerErrorExceptionTest() throws UnauthorizedException, ServletException, IOException, NotFoundException, SQLException {

        Map<String, String> map = new HashMap<>();
        map.put("email", "yannick.dohmen@test.de");
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        DoubleAuthAdapter doubleAuthAdapter = mock(DoubleAuthAdapter.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);
        when(doubleAuthAdapter.getCodeById(anyInt())).thenThrow(SQLException.class);
        doubleAuthController.doubleAuthAdapter = doubleAuthAdapter;

        // Creates code
        doubleAuthController.doPost(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());



        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertEquals(500,returnedResponseEnity.getStatus());
    }

    @Test
    public void doGetTest() throws ServletException, IOException, NotFoundException, SQLException {

        DoubleAuthAdapter doubleAuthAdapter = mock(DoubleAuthAdapter.class);
        when(doubleAuthAdapter.getCode(anyString(), anyInt())).thenReturn(55555);
        doubleAuthController.doubleAuthAdapter = doubleAuthAdapter;

        Map<String, String> map = new HashMap<>();
        map.put("email", "yannick.dohmen@test.de");
        map.put("code", "55555");
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);

        // Creates code
        doubleAuthController.doGet(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());

        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertTrue(returnedResponseEnity.getData());
        Assert.assertEquals(200, returnedResponseEnity.getStatus());
    }

    @Test
    public void doGetBadRequestExceptionTest() throws ServletException, IOException, NotFoundException, SQLException {

        DoubleAuthAdapter doubleAuthAdapter = mock(DoubleAuthAdapter.class);
        when(doubleAuthAdapter.getCode(anyString(), anyInt())).thenReturn(55555);
        doubleAuthController.doubleAuthAdapter = doubleAuthAdapter;

        Map<String, String> map = new HashMap<>();
        map.put("email", null);
        map.put("code", "55555");
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);

        // Creates code
        doubleAuthController.doGet(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());

        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertFalse(returnedResponseEnity.getData());
        Assert.assertEquals(400, returnedResponseEnity.getStatus());
    }

    @Test
    public void doGetNotFoundExceptionTest() throws ServletException, IOException, NotFoundException, SQLException {

        DoubleAuthAdapter doubleAuthAdapter = mock(DoubleAuthAdapter.class);
        when(doubleAuthAdapter.getCode(anyString(), anyInt())).thenThrow(NotFoundException.class);
        doubleAuthController.doubleAuthAdapter = doubleAuthAdapter;

        Map<String, String> map = new HashMap<>();
        map.put("email", "yannick.dohmen@test.de");
        map.put("code", "55555");
        doReturn(map).when(doubleAuthController).getQueryParameters(any());
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        Writer writer = mock(Writer.class);
        PrintWriter out = new PrintWriter(writer);
        when(resp.getWriter()).thenReturn(out);

        // Creates code
        doubleAuthController.doGet(req, resp);

        final ArgumentCaptor<HttpServletResponse> captorUseles = ArgumentCaptor.forClass(HttpServletResponse.class);
        final ArgumentCaptor<ResponseEntity<Boolean>> captor = ArgumentCaptor.forClass(ResponseEntity.class);
        verify(doubleAuthController).sendResponse(captorUseles.capture(), captor.capture());

        final ResponseEntity<Boolean> returnedResponseEnity = captor.getValue();

        Assert.assertFalse(returnedResponseEnity.getData());
        Assert.assertEquals(404, returnedResponseEnity.getStatus());
    }



/*
    map = new HashMap<>();
        map.put("email", "yannick.dohmen@test.de");
        map.put("code", code);
    doReturn(map).when(doubleAuthController).getQueryParameters(any());

        doubleAuthController.doGet(req, resp);

 */
}
