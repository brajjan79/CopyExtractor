package com.github.extractor;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.configuration.ConfigFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ App.class, Cli.class, ConfigFactory.class })
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
        final Executor mockExecutor = createMockExecutor();
        PowerMockito.doThrow(new RuntimeException()).when(mockExecutor, "run");
        App.main(new String[] {});
    }

    @Test
    public void systemExitWithAllWell() throws Throwable {
        final Executor mockExecutor = createMockExecutor();
        PowerMockito.doNothing().when(mockExecutor, "run");
        try {
            App.main(new String[] {});
        } catch (final Exception e) {
            fail("Unexpected exception.");
        }
    }

    private Executor createMockExecutor() throws Exception {
        PowerMockito.mockStatic(Cli.class);
        PowerMockito.mockStatic(ConfigFactory.class);

        final Executor mockExecutor = PowerMockito.mock(Executor.class);
        PowerMockito.whenNew(Executor.class).withAnyArguments().thenReturn(mockExecutor);
        return mockExecutor;
    }
}
