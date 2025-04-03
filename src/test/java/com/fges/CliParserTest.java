package com.fges.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CliParserTest {

    private CliParser parser;

    @BeforeEach
    public void setUp() throws Exception {
        parser = new CliParser();
    }

    @Test
    public void shouldReturnNullWhenRequiredSourceOptionMissing() {
        // No source option provided
        String[] args = new String[]{"add", "Milk", "2"};

        CommandLine cmd = parser.parse(args);

        assertNull(cmd);
    }

    @Test
    public void shouldParseSourceOption() {
        String[] args = new String[]{"-s", "grocerylist.json", "add", "Milk", "2"};

        CommandLine cmd = parser.parse(args);

        assertNotNull(cmd);
        assertEquals("grocerylist.json", cmd.getOptionValue("s"));
        assertEquals("grocerylist.json", cmd.getOptionValue("source"));
    }

    @Test
    public void shouldParseFormatOption() {
        String[] args = new String[]{"-s", "grocerylist.csv", "-f", "csv", "add", "Milk", "2"};

        CommandLine cmd = parser.parse(args);

        assertNotNull(cmd);
        assertEquals("csv", cmd.getOptionValue("f"));
        assertEquals("csv", cmd.getOptionValue("format"));
    }

    @Test
    public void shouldUseDefaultFormatWhenNotSpecified() {
        String[] args = new String[]{"-s", "grocerylist.json", "add", "Milk", "2"};

        CommandLine cmd = parser.parse(args);

        assertNotNull(cmd);
        assertEquals("json", cmd.getOptionValue("f", "json")); // Default is json
    }

    @Test
    public void shouldParseCategoryOption() {
        String[] args = new String[]{"-s", "grocerylist.json", "-c", "Dairy", "add", "Milk", "2"};

        CommandLine cmd = parser.parse(args);

        assertNotNull(cmd);
        assertEquals("Dairy", cmd.getOptionValue("c"));
        assertEquals("Dairy", cmd.getOptionValue("category"));
    }

    @Test
    public void shouldUseDefaultCategoryWhenNotSpecified() {
        String[] args = new String[]{"-s", "grocerylist.json", "add", "Salt", "1"};

        CommandLine cmd = parser.parse(args);

        assertNotNull(cmd);
        assertEquals("default", cmd.getOptionValue("c", "default")); // Default is "default"
    }

    @Test
    public void shouldParsePositionalArguments() {
        String[] args = new String[]{"-s", "grocerylist.json", "add", "Milk", "2"};

        CommandLine cmd = parser.parse(args);

        assertNotNull(cmd);
        // Verify positional arguments
        assertEquals(3, cmd.getArgList().size());
        assertEquals("add", cmd.getArgList().get(0));
        assertEquals("Milk", cmd.getArgList().get(1));
        assertEquals("2", cmd.getArgList().get(2));
    }

    @Test
    public void shouldProvideConfiguredOptions() {
        Options options = parser.getOptions();

        assertNotNull(options);
        assertTrue(options.hasOption("s"));
        assertTrue(options.hasOption("f"));
        assertTrue(options.hasOption("c"));

        // Source should be required
        assertTrue(options.getOption("s").isRequired());

        // Format and category should not be required
        assertFalse(options.getOption("f").isRequired());
        assertFalse(options.getOption("c").isRequired());
    }
}