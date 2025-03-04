package com.ontic.test.base;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

/**
 * @author rajesh
 * @since 04/03/25 16:28
 */
public class TestFrameworkLauncherListener implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        Fixtures.setUp();
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        Fixtures.tearDown();
    }
}
