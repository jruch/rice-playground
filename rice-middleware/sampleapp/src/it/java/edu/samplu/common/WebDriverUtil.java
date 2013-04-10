/*
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.samplu.common;

import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import com.thoughtworks.selenium.SeleneseTestBase;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * The goal of the WebDriverUtil class is to invert the dependencies on WebDriver from WebDriverLegacyITBase for reuse
 * without having to extend WebDriverLegacyITBase.  For the first example see waitFor
 *
 * @see WebDriverLegacyITBase
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WebDriverUtil {

    /**
     * TODO apparent dup WebDriverITBase.DEFAULT_WAIT_SEC
     * TODO parametrize for JVM Arg
     * 30 Seconds
     */
    public static int DEFAULT_IMPLICIT_WAIT_TIME = 30;

    /**
     * TODO introduce SHORT_IMPLICIT_WAIT_TIME with param in WebDriverITBase
     * TODO parametrize for JVM Arg
     * 1 Second
     */
    public static int SHORT_IMPLICIT_WAIT_TIME = 1;

    /**
     * Set -Dremote.driver.saucelabs for running on saucelabs
     * @link https://wiki.kuali.org/display/KULRICE/How+To+Run+a+Selenium+Test for patch required
     */
    public static final String REMOTE_DRIVER_SAUCELABS_PROPERTY = "remote.driver.saucelabs";

    /**
     * Selenium's webdriver.chrome.driver parameter, you can set -Dwebdriver.chrome.driver= or Rice's REMOTE_PUBLIC_CHROME
     */
    public static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";

    /**
     * Set -Dremote.public.chrome= or WEBDRIVER_CHROME_DRIVER
     */
    public static final String REMOTE_PUBLIC_CHROME = "remote.public.chrome";

    /**
     * Time to wait for the URL used in setup to load.  Sometimes this is the first hit on the app and it needs a bit
     * longer than any other.  120 Seconds.
     * TODO parametrize for JVM Arg
     */
    public static final int SETUP_URL_LOAD_WAIT_SECONDS = 120;
    
    /**
     * https://jira.kuali.org/browse/
     */
    public static final String JIRA_BROWSE_URL = "https://jira.kuali.org/browse/";
    static Map<String, String> jiraMatches;
    static {
        jiraMatches = new HashMap<String, String>();
        jiraMatches.put("Error setting property values; nested exception is org.springframework.beans.NotWritablePropertyException: Invalid property 'refreshWhenChanged' of bean class [org.kuali.rice.krad.uif.element.Action]: Bean property 'refreshWhenChanged' is not writable or has an invalid setter method. Does the parameter type of the setter match the return type of the getter?",
                "KULRICE-8137 Agenda Rule edit Incident report Invalid property 'refreshWhenChanged'");

        jiraMatches.put("org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase.processAddCollectionLineBusinessRules(MaintenanceDocumentRuleBase.",
                "KULRICE-8142 NPE in MaintenanceDocumentRuleBase.processAddCollectionLineBusinessRules");

        jiraMatches.put("at org.kuali.rice.krad.rules.DocumentRuleBase.isDocumentOverviewValid(DocumentRuleBase.",
                "KULRICE-8134 NPE in DocumentRuleBase.isDocumentOverviewValid(DocumentRuleBase");

        jiraMatches.put("org.kuali.rice.krad.uif.layout.TableLayoutManager.buildLine(TableLayoutManager.",
                "KULRICE-8160 NPE at TableLayoutManager.buildLine(TableLayoutManager");

        jiraMatches.put("Bean property 'configFileLocations' is not writable or has an invalid setter method. Does the parameter type of the setter match the return type of the getter?",
                "KULRICE-8173 Bean property 'configFileLocations' is not writable or has an invalid setter method");

        jiraMatches.put("Bean property 'componentSecurity' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?",
                "KULRICE-8182 JDK7 Bean property 'componentSecurity' is not readable...");

        jiraMatches.put("java.sql.SQLSyntaxErrorException: ORA-00904: \"ROUTEHEADERID\": invalid identifier",
                "KULRICE-8277 Several ITs fail with OJB operation; bad SQL grammar []; nested exception is java.sql.SQLException: ORA-00904: \"ROUTEHEADERID\": invalid identifier");

        jiraMatches.put("By.xpath: //button[@data-loadingmessage='Adding Line...']",
                "KULRICE-9044 KRAD \"stacked\" collection elements are not rendering add/delete buttons ");

        jiraMatches.put("Error: on line 135, column 39 in krad/WEB-INF/ftl/lib/grid.ftl",
                "KULRICE-9047 Term maintenance freemarker exception ");
    }
    
    /**
     * Setup the WebDriver test, login, and load the given web page
     *
     * @param username
     * @param url
     * @return driver
     * @throws Exception
     */
    public static WebDriver setUp(String username, String url) throws Exception {
        return setUp(username, url, null, null);
    }

    /**
     * Setup the WebDriver test, login, and load the given web page
     *
     * @param username
     * @param url
     * @param className
     * @param testName
     * @return driver
     * @throws Exception
     */
    public static WebDriver setUp(String username, String url, String className, TestName testName) throws Exception {
        WebDriver driver = null;
        if (System.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY) == null) {
            driver = getWebDriver();
//        } else {
//            SauceLabsWebDriverHelper saucelabs = new SauceLabsWebDriverHelper();
//            saucelabs.setUp(className, testName);
//            driver = saucelabs.getDriver();
        }
        driver.manage().timeouts().implicitlyWait(SETUP_URL_LOAD_WAIT_SECONDS, TimeUnit.SECONDS);

        // TODO Got into the situation where the first url doesn't expect server, but all others do.  Readdress once
        // the NavIT WDIT conversion has been completed.
        if (!url.startsWith("http")) {
            url = ITUtil.getBaseUrlString() + url;
        }

        driver.get(url);
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        return driver;
    }

    /**
     *
     * @param passed
     * @param sessionId
     * @param testParam
     * @param userParam
     * @throws Exception
     */
    public static void tearDown(boolean passed, String sessionId, String testParam, String userParam) throws Exception {

//        if (System.getProperty(SauceLabsWebDriverHelper.SAUCE_PROPERTY) != null) {
//            SauceLabsWebDriverHelper.tearDown(passed, sessionId, System.getProperty(SauceLabsWebDriverHelper.SAUCE_USER_PROPERTY),
//                    System.getProperty(SauceLabsWebDriverHelper.SAUCE_KEY_PROPERTY));
//        }

        if (System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) {
            ITUtil.getHTML(ITUtil.prettyHttp(System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test="
                    + testParam + "&user=" + userParam));
        }
    }

    /**
     *
     * @param testParam
     * @return
     */
    public static String determineUser(String testParam) {
        String user = null;

        if (System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USER_PROPERTY) != null) {
            return System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USER_PROPERTY);
        } else if (System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) { // deprecated
            String userResponse = ITUtil.getHTML(ITUtil.prettyHttp(System.getProperty(
                    WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test=" + testParam.trim()));
            return userResponse.substring(userResponse.lastIndexOf(":") + 2, userResponse.lastIndexOf("\""));
        }

        return user;
    }

    /***
     * @link ITUtil#checkForIncidentReport
     * @param driver
     * @param locator
     * @param message
     */
    public static void checkForIncidentReport(WebDriver driver, String locator, Failable failable,
            String message) {
        ITUtil.checkForIncidentReport(driver.getPageSource(), locator, failable, message);
    }

    /**
     * @link http://code.google.com/p/chromedriver/downloads/list
     * @link #REMOTE_PUBLIC_CHROME
     * @link #WEBDRIVER_CHROME_DRIVER
     * @link ITUtil#HUB_DRIVER_PROPERTY
     * @return chromeDriverService
     */
    public static ChromeDriverService chromeDriverCreateCheck() {
        String driverParam = System.getProperty(ITUtil.HUB_DRIVER_PROPERTY);
        // TODO can the saucelabs driver stuff be leveraged here?
        if (driverParam != null && "chrome".equals(driverParam.toLowerCase())) {
            if (System.getProperty(WEBDRIVER_CHROME_DRIVER) == null) {
                if (System.getProperty(REMOTE_PUBLIC_CHROME) != null) {
                    System.setProperty(WEBDRIVER_CHROME_DRIVER, System.getProperty(REMOTE_PUBLIC_CHROME));
                }
            }
            try {
                ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                        .usingDriverExecutable(new File(System.getProperty(WEBDRIVER_CHROME_DRIVER)))
                        .usingAnyFreePort()
                        .build();
                return chromeDriverService;
            } catch (Throwable t) {
                throw new RuntimeException("Exception starting chrome driver service, is chromedriver ( http://code.google.com/p/chromedriver/downloads/list ) installed? You can include the path to it using -Dremote.public.chrome", t)   ;
            }
        }
        return null;
    }

    /**
     * remote.public.driver set to chrome or firefox (null assumes firefox)
     * if remote.public.hub is set a RemoteWebDriver is created (Selenium Grid)
     * @return WebDriver or null if unable to create
     */
    public static WebDriver getWebDriver() {
        String driverParam = System.getProperty(ITUtil.HUB_DRIVER_PROPERTY);
        String hubParam = System.getProperty(ITUtil.HUB_PROPERTY);
        if (hubParam == null) {
            if (driverParam == null || "firefox".equalsIgnoreCase(driverParam)) {
                FirefoxProfile profile = new FirefoxProfile();
                profile.setEnableNativeEvents(false);
                return new FirefoxDriver(profile);
            } else if ("chrome".equalsIgnoreCase(driverParam)) {
                return new ChromeDriver();
            } else if ("safari".equals(driverParam)) {
                System.out.println("SafariDriver probably won't work, if it does please contact Erik M.");
                return new SafariDriver();
            }
        } else {
            try {
                if (driverParam == null || "firefox".equalsIgnoreCase(driverParam)) {
                    return new RemoteWebDriver(new URL(ITUtil.getHubUrlString()), DesiredCapabilities.firefox());
                } else if ("chrome".equalsIgnoreCase(driverParam)) {
                    return new RemoteWebDriver(new URL(ITUtil.getHubUrlString()), DesiredCapabilities.chrome());
                }
            } catch (MalformedURLException mue) {
                System.out.println(ITUtil.getHubUrlString() + " " + mue.getMessage());
                mue.printStackTrace();
            }
        }
        return null;
    }

    /**
     * If the JVM arg remote.autologin is set, auto login as admin will not be done.
     * @param driver
     * @param userName
     * @param failable
     * @throws InterruptedException
     */
    public static void login(WebDriver driver, String userName, Failable failable) throws InterruptedException {
        if (System.getProperty(ITUtil.REMOTE_AUTOLOGIN_PROPERTY) == null) {
            driver.findElement(By.name("__login_user")).clear();
            driver.findElement(By.name("__login_user")).sendKeys(userName);
            driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
            Thread.sleep(1000);
            String contents = driver.getPageSource();
            ITUtil.failOnInvalidUserName(userName, contents, failable);
        }
    }

    protected static void selectFrameSafe(WebDriver driver, String locator) {
        try {
            driver.switchTo().frame(locator);
        } catch (NoSuchFrameException nsfe) {
            // don't fail
        }
    }

    /**
     * Wait for the given amount of seconds, for the given by, using the given driver.  The message is displayed if the
     * by cannot be found.  No action is performed on the by, so it is possible that the by found is not visible or enabled.
     *
     * @param driver WebDriver
     * @param waitSeconds int
     * @param by By
     * @param message String
     * @throws InterruptedException
     */
    public static void waitFor(WebDriver driver, int waitSeconds, By by, String message) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        Thread.sleep(1000);
        driver.findElement(by);  // NOTICE just the find, no action, so by is found, but might not be visible or enabled.
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }
    
    public static void failOnMatchedJira(String contents) {
        Iterator<String> iter = jiraMatches.keySet().iterator();
        String key = null;

        while (iter.hasNext()) {
            key = iter.next();
            if (contents.contains(key)) {
                SeleneseTestBase.fail(JIRA_BROWSE_URL + jiraMatches.get(key));
            }
        }
    }
    
    private static void failWithReportInfoForKim(String contents, String linkLocator, String message) {
        final String kimIncidentReport = extractIncidentReportKim(contents, linkLocator, message);
        SeleneseTestBase.fail(kimIncidentReport);
    }
    
    private static String extractIncidentReportKim(String contents, String linkLocator, String message) {
        String chunk =  contents.substring(contents.indexOf("id=\"headerarea\""), contents.lastIndexOf("</div>") );
        String docIdPre = "type=\"hidden\" value=\"";
        String docId = chunk.substring(chunk.indexOf(docIdPre) + docIdPre.length(), chunk.indexOf("\" name=\"documentId\""));

        String stackTrace = chunk.substring(chunk.lastIndexOf("name=\"displayMessage\""), chunk.length());
        String stackTracePre = "value=\"";
        stackTrace = stackTrace.substring(stackTrace.indexOf(stackTracePre) + stackTracePre.length(), stackTrace.indexOf("name=\"stackTrace\"") - 2);

        return "\nIncident report "+ message+ " navigating to "+ linkLocator + " Doc Id: "+ docId.trim()+ "\nStackTrace: "+ stackTrace.trim();
    }
    
    private static void processIncidentReport(String contents, String linkLocator, String message) {
        failOnMatchedJira(contents);

        if (contents.indexOf("Incident Feedback") > -1) {
            failWithReportInfo(contents, linkLocator, message);
        }

        if (contents.indexOf("Incident Report") > -1) { // KIM incident report
            failWithReportInfoForKim(contents, linkLocator, message);
        }

        SeleneseTestBase.fail("\nIncident report detected " + message + "\n Unable to parse out details for the contents that triggered exception: " + deLinespace(
                contents));
    }

    private static void failWithReportInfo(String contents, String linkLocator, String message) {
        final String incidentReportInformation = extractIncidentReportInfo(contents, linkLocator, message);
        SeleneseTestBase.fail(incidentReportInformation);
    }
    
    private static String extractIncidentReportInfo(String contents, String linkLocator, String message) {
        String chunk =  contents.substring(contents.indexOf("Incident Feedback"), contents.lastIndexOf("</div>") );
        String docId = chunk.substring(chunk.lastIndexOf("Document Id"), chunk.indexOf("View Id"));
        docId = docId.substring(0, docId.indexOf("</span>"));
        docId = docId.substring(docId.lastIndexOf(">") + 2, docId.length());

        String viewId = chunk.substring(chunk.lastIndexOf("View Id"), chunk.indexOf("Error Message"));
        viewId = viewId.substring(0, viewId.indexOf("</span>"));
        viewId = viewId.substring(viewId.lastIndexOf(">") + 2, viewId.length());

        String stackTrace = chunk.substring(chunk.lastIndexOf("(only in dev mode)"), chunk.length());
        stackTrace = stackTrace.substring(stackTrace.indexOf("<span id=\"") + 3, stackTrace.length());
        stackTrace = stackTrace.substring(stackTrace.indexOf("\">") + 2, stackTrace.indexOf("</span>"));
    
        return "\nIncident report "+ message+ " navigating to "+ linkLocator+ " : View Id: "+ viewId.trim()+ " Doc Id: "+ docId.trim()+ "\nStackTrace: "+ stackTrace.trim();
    }
    
    public static String deLinespace(String contents) {
        while (contents.contains("\n\n")) {
            contents = contents.replaceAll("\n\n", "\n");
        }
        
        return contents;
    }
}
