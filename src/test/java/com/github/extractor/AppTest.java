package com.github.extractor;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.extractor.configuration.Cli;
import com.github.extractor.configuration.ConfigFactory;

public class AppTest {

    @Test
    public void testConstructor() {
        try {
            new App();
        } catch (final Exception e) {
            fail("Could not initiate App.");
        }
    }

    @Test
    public void systemExitWithHelpArgs() {
        try {
            App.main(new String[] {"--help"});
        } catch (final Exception e) {
            fail("--help arg raised exxception.");
        }
    }

    @Test(expected=RuntimeException.class)
    public void systemExitWithInvalidArgs() {
        App.main(new String[] {"--extract-path", "path"});
    }

    @Test(expected=RuntimeException.class)
    public void systemExitWithNoArgs() {
        App.main(new String[] {});
    }

    @Test(expected=RuntimeException.class)
    public void systemExitWithExecutorError() throws Throwable {
        try (MockedStatic<Cli> cli = Mockito.mockStatic(Cli.class);
                MockedStatic<ConfigFactory> configFactory = Mockito.mockStatic(ConfigFactory.class);
                MockedConstruction<Executor> mockExecutor = Mockito.mockConstruction(Executor.class,
                        (mock, context) -> {
                            Mockito.doThrow(new RuntimeException()).when(mock).run();
                        })) {

            App.main(new String[] {});
        }
    }

    @Test
    public void systemExitWithAllWell() throws Throwable {
        try (MockedStatic<Cli> cli = Mockito.mockStatic(Cli.class);
                MockedStatic<ConfigFactory> configFactory = Mockito.mockStatic(ConfigFactory.class);
                MockedConstruction<Executor> mockExecutor = Mockito.mockConstruction(Executor.class)) {
            App.main(new String[] {});
        } catch (final Exception e) {
            fail("Unexpected exception.");
        }
    }

}
