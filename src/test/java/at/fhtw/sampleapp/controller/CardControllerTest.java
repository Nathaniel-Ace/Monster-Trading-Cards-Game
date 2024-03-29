package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.service.CardService;
import at.fhtw.sampleapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CardControllerTest {

    private CardController cardController;
    private CardService cardService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        cardService = mock(CardService.class);
        cardController = spy(new CardController());

        Field cardServiceField = CardController.class.getDeclaredField("cardService");
        cardServiceField.setAccessible(true);
        cardServiceField.set(cardController, cardService);

        // Stub the checkToken method to return a valid username when given a valid token
        doReturn("validUsername").when(cardController).checkToken("validToken");
    }

    private Request createMockRequest(String token) {
        Request mockRequest = mock(Request.class);
        HeaderMap mockHeaderMap = mock(HeaderMap.class);

        when(mockRequest.getMethod()).thenReturn(Method.GET);
        when(mockRequest.getPathname()).thenReturn("/cards");
        when(mockRequest.getHeaderMap()).thenReturn(mockHeaderMap);
        when(mockHeaderMap.getHeader("Authorization")).thenReturn(token);

        return mockRequest;
    }

    @Test
    void handleRequest_GET_ValidToken() {
        // Arrange
        Request getRequest = createMockRequest("validToken");
        Map<String, Object> card = Collections.singletonMap("card", "testCard");
        when(cardService.getCards(anyString())).thenReturn(Collections.singletonList(card));
        Response expectedResponse = new Response(HttpStatus.OK, ContentType.JSON, "The user has cards, the response contains these: \n[{\"card\":\"testCard\"}]");

        // Act
        Response actualResponse = cardController.handleRequest(getRequest);

        // Assert
        verify(cardService, times(1)).getCards(anyString());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    @Test
    void handleRequest_GET_NoCards() {
        // Arrange
        Request getRequest = createMockRequest("validToken");
        when(cardService.getCards(anyString())).thenThrow(new RuntimeException("No cards found"));
        Response expectedResponse = new Response(HttpStatus.ACCEPTED, ContentType.JSON, "The request was fine, but the user doesn't have any cards");

        // Act
        Response actualResponse = cardController.handleRequest(getRequest);

        // Assert
        verify(cardService, times(1)).getCards(anyString());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }

    @Test
    void handleRequest_GET_InvalidToken() {
        // Arrange
        Request getRequest = createMockRequest("invalidToken");
        Response expectedResponse = new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        // Act
        Response actualResponse = cardController.handleRequest(getRequest);

        // Assert
        verify(cardService, times(0)).getCards(anyString());
        assertEquals(expectedResponse.get(), actualResponse.get());
    }
}