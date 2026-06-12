package io.mobtest.agentic.support;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * Reads {@code <parameter name="platform" .../>} from the TestNG suite XML
 * so ios.xml / android.xml work without passing -Dplatform on the command line.
 */
public class PlatformSuiteListener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        String platform = suite.getXmlSuite().getParameter("platform");
        if (platform != null && !platform.isBlank()) {
            System.setProperty("platform", platform.trim());
        }
    }
}
