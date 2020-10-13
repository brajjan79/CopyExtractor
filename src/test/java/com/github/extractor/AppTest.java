package com.github.extractor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.configuration.ConfigFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ App.class, Cli.class, ConfigFactory.class })
public class AppTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testConstructor() {
        new App();
    }

    @Test
    public void systemExitWithHelpArgs() {
        exit.expectSystemExitWithStatus(1);
        App.main(new String[] {"--help"});
    }

    @Test
    public void systemExitWithInvalidArgs() {
        exit.expectSystemExitWithStatus(1);
        App.main(new String[] {"--extract-path", "path"});
    }

    @Test
    public void systemExitWithNoArgs() {
        exit.expectSystemExitWithStatus(1);
        App.main(new String[] {});
    }

    @Test
    public void systemExitWithExecutorError() throws Throwable {
        exit.expectSystemExitWithStatus(1);
        final Executor mockExecutor = createMockExecutor();
        PowerMockito.doThrow(new RuntimeException()).when(mockExecutor, "run");
        App.main(new String[] {});
    }

    @Test
    public void systemExitWithAllWell() throws Throwable {
        final Executor mockExecutor = createMockExecutor();
        PowerMockito.doNothing().when(mockExecutor, "run");
        App.main(new String[] {});
    }

    private Executor createMockExecutor() throws Exception {
        PowerMockito.mockStatic(Cli.class);
        PowerMockito.mockStatic(ConfigFactory.class);

        final Executor mockExecutor = PowerMockito.mock(Executor.class);
        PowerMockito.whenNew(Executor.class).withAnyArguments().thenReturn(mockExecutor);
        return mockExecutor;
    }
}
