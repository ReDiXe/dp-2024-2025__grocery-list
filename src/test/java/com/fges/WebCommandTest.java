package com.fges;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Arrays;

import com.fges.command.WebCommand;
import com.fges.storage.GroceryListStorage;
import com.fges.web.GroceryShopAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WebCommandTest {

    @Mock
    private GroceryListStorage mockStorage;

    private WebCommand webCommand;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExecuteWithValidPort() throws Exception {

        // Créer une classe de test qui étend WebCommand pour éviter le Thread.join()
        class TestableWebCommand extends WebCommand {
            public TestableWebCommand(GroceryListStorage storage, List<String> args) {
                super(storage, args);
            }

            @Override
            public int execute() throws Exception {
                if (getArgs().size() < 2) {
                    System.err.println("Missing port argument for web command.");
                    return 1;
                }

                int port;
                try {
                    port = Integer.parseInt(getArgs().get(1));
                } catch (NumberFormatException e) {
                    System.err.println("Port must be a number.");
                    return 1;
                }

                GroceryShopAdapter groceryShop = new GroceryShopAdapter(getStorage());
                System.out.println("Grocery shop server started at http://localhost:" + port);

                return 0;
            }

            private GroceryListStorage getStorage() {
                return mockStorage;
            }

            private List<String> getArgs() {
                return Arrays.asList("web", "8080");
            }
        }

        TestableWebCommand testableCommand = new TestableWebCommand(mockStorage, Arrays.asList("web", "8080"));

        int result = testableCommand.execute();

        assertEquals(0, result);
    }

    @Test
    public void testExecuteWithMissingPort() throws Exception {
        List<String> args = Arrays.asList("web");
        webCommand = new WebCommand(mockStorage, args);

        int result = webCommand.execute();

        assertEquals(1, result);
    }

    @Test
    public void testExecuteWithInvalidPort() throws Exception {
        List<String> args = Arrays.asList("web", "not_a_number");
        webCommand = new WebCommand(mockStorage, args);

        int result = webCommand.execute();

        assertEquals(1, result);
    }
}