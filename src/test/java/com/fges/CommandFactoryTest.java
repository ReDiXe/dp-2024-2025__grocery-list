package com.fges.command;

import com.fges.storage.GroceryListStorage;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommandFactoryTest {

    @Mock
    private GroceryListStorage mockStorage;

    @Mock
    private CommandLine mockCmd;

    private List<String> args;

    @BeforeEach
    public void setUp() throws Exception {
        args = new ArrayList<>();
    }

    @Test
    public void shouldCreateAddCommand() {
        args.add("add");

        Command command = CommandFactory.getCommand("add", mockStorage, args, mockCmd);

        assertNotNull(command);
        assertTrue(command instanceof AddCommand);
    }

    @Test
    public void shouldCreateListCommand() {
        args.add("list");

        Command command = CommandFactory.getCommand("list", mockStorage, args, mockCmd);

        assertNotNull(command);
        assertTrue(command instanceof ListCommand);
    }

    @Test
    public void shouldCreateRemoveCommand() {
        args.add("remove");

        Command command = CommandFactory.getCommand("remove", mockStorage, args, mockCmd);

        assertNotNull(command);
        assertTrue(command instanceof RemoveCommand);
    }

    @Test
    public void shouldReturnNullForUnknownCommand() {
        args.add("unknown");

        Command command = CommandFactory.getCommand("unknown", mockStorage, args, mockCmd);

        assertNull(command);
    }

    @Test
    public void shouldHandleCaseSensitiveCommands() {
        // Test with uppercase command names - should return null as matches are case-sensitive
        Command addCommand = CommandFactory.getCommand("ADD", mockStorage, args, mockCmd);
        Command listCommand = CommandFactory.getCommand("LIST", mockStorage, args, mockCmd);
        Command removeCommand = CommandFactory.getCommand("REMOVE", mockStorage, args, mockCmd);

        assertNull(addCommand);
        assertNull(listCommand);
        assertNull(removeCommand);
    }
}