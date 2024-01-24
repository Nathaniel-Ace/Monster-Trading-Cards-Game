package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.service.BattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class BattleControllerTest {

    private BattleController battleController;
    private BattleService battleService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        battleService = Mockito.mock(BattleService.class);
        battleController = Mockito.spy(new BattleController());

        Field battleServiceField = BattleController.class.getDeclaredField("battleService");
        battleServiceField.setAccessible(true);
        battleServiceField.set(battleController, battleService);

        // Stub the checkToken method to return a valid username when given a valid token
        Mockito.doReturn("validUsername").when(battleController).checkToken("validToken");
    }

    private Request createMockRequest(Method method, String token, String pathname) {
        Request mockRequest = Mockito.mock(Request.class);
        HeaderMap mockHeaderMap = Mockito.mock(HeaderMap.class);

        Mockito.when(mockRequest.getMethod()).thenReturn(method);
        Mockito.when(mockRequest.getPathname()).thenReturn(pathname);
        Mockito.when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        Mockito.when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    private void assertResponseEquals(Response expected, Response actual) {
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    void handleRequest_POST_ValidToken_Battles() {
        // Arrange
        Request postRequest = createMockRequest(Method.POST, "validToken", "/battles");
        when(battleService.startBattle(anyString(), anyString())).thenReturn("Battle started");
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "Waiting for opponent\n");

        // Act
        Response actualResponse = battleController.handleRequest(postRequest);

        // Assert
        assertResponseEquals(expectedResponse, actualResponse);
    }

    @Test
    void handleRequest_POST_InvalidToken_Battles() {
        // Arrange
        Request postRequest = createMockRequest(Method.POST, null, "/battles");
        doReturn(null).when(battleController).checkToken("invalidToken");
        Response expectedResponse = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        // Act
        Response actualResponse = battleController.handleRequest(postRequest);

        // Assert
        assertResponseEquals(expectedResponse, actualResponse);
    }

}