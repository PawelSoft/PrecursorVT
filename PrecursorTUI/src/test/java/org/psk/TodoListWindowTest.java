package org.psk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

public class TodoListWindowTest {

    private TodoListWindow todoListWindow;

    @BeforeEach
    public void setUp() {
        todoListWindow = new TodoListWindow(null, null, null);
    }

    @Test
    @DisplayName("Test convertToDate with valid date strings")
    public void testConvertToDate_ValidDates() {
        assertDoesNotThrow(() -> {
            LocalDateTime dateTime = TodoListWindow.convertToDate("01.01.2023 12.00", "dd.MM.yyyy HH.mm");
            assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), dateTime);
        });
    }

    @Test
    @DisplayName("Test convertToDate with invalid date strings")
    public void testConvertToDate_InvalidDates() {
        assertThrows(DateTimeParseException.class,
                () -> TodoListWindow.convertToDate("invalid-date", "dd.MM.yyyy HH.mm"));
    }

    @ParameterizedTest(name = "convertToDate should parse {0} to {1}")
    @CsvSource({
            "01.01.2023 12.00, 2023-01-01T12:00",
            "31.12.2023 23.59, 2023-12-31T23:59",
            "15.06.2023 08.30, 2023-06-15T08:30"
    })
    public void testConvertToDate_Parameterized(String input, String expected) {
        LocalDateTime expectedDateTime = LocalDateTime.parse(expected);
        LocalDateTime actualDateTime = TodoListWindow.convertToDate(input, "dd.MM.yyyy HH.mm");
        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    @DisplayName("Test convertToString with valid LocalDateTime")
    public void testConvertToString_ValidLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        String formattedDate = TodoListWindow.convertToString(dateTime, "dd.MM.yyyy HH.mm");
        assertEquals("01.01.2023 12.00", formattedDate);
    }

    @ParameterizedTest(name = "convertToString should format {0} to {1}")
    @CsvSource({
            "2023-01-01T12:00, 01.01.2023 12.00",
            "2023-12-31T23:59, 31.12.2023 23.59",
            "2023-06-15T08:30, 15.06.2023 08.30"
    })
    public void testConvertToString_Parameterized(String input, String expected) {
        LocalDateTime dateTime = LocalDateTime.parse(input);
        String formattedDate = TodoListWindow.convertToString(dateTime, "dd.MM.yyyy HH.mm");
        assertEquals(expected, formattedDate);
    }
}
