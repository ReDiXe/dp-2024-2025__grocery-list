package com.fges.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitaire de la classe InfoCommand
 */
public class InfoCommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void shouldDisplayCorrectSystemInfo() throws Exception {
        InfoCommand command = new InfoCommand();

        int result = command.execute();

        assertEquals(0, result);

        String output = outContent.toString();

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(output.contains("Today's date: " + today));

        assertTrue(output.contains("Operating System: " + System.getProperty("os.name")));

        assertTrue(output.contains("Java version: " + System.getProperty("java.version")));
    }
}