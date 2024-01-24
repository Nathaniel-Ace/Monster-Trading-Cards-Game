package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class SessionControllerTest {

    private Request createMockRequest(Method method, String path, String body) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(method);
        when(mockRequest.getPathname()).thenReturn(path);
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockRequest.getBody()).thenReturn(body);

        return mockRequest;
    }

    @Test
    void handlePostRequestWithValidCredentials() {
        // Arrange
        SessionController sessionController = spy(new SessionController());
        Response response = mock(Response.class);

        // Create a mock Request with a body containing Username and Password in JSON format
        Request postRequest = createMockRequest(Method.POST, "/session", "{\"Username\": \"validUser\", \"Password\": \"validPassword\"}");

        doReturn(response).when(sessionController).handleRequest(postRequest);

        // Act
        sessionController.handleRequest(postRequest);

        // Assert
        verify(sessionController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }

    @Test
    void handlePostRequestWithInvalidCredentials() {
        // Arrange
        SessionController sessionController = spy(new SessionController());
        Response response = mock(Response.class);

        // Create a mock Request with a body containing Username and Password in JSON format
        Request postRequest = createMockRequest(Method.POST, "/session", "{\"Username\": \"invalidUser\", \"Password\": \"invalidPassword\"}");

        doReturn(response).when(sessionController).handleRequest(postRequest);

        // Act
        sessionController.handleRequest(postRequest);

        // Assert
        verify(sessionController, times(1)).handleRequest(postRequest);
        // Add more assertions based on the expected behavior of your handleRequest method
    }
}