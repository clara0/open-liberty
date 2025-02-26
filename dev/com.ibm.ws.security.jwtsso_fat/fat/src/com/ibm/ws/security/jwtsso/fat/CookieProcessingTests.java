/*******************************************************************************
 * Copyright (c) 2018, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.jwtsso.fat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.security.fat.common.CommonSecurityFat;
import com.ibm.ws.security.fat.common.actions.TestActions;
import com.ibm.ws.security.fat.common.expectations.Expectations;
import com.ibm.ws.security.fat.common.utils.CommonWaitForAppChecks;
import com.ibm.ws.security.fat.common.utils.FatStringUtils;
import com.ibm.ws.security.fat.common.validation.TestValidationUtils;
import com.ibm.ws.security.jwtsso.fat.actions.JwtFatActions;
import com.ibm.ws.security.jwtsso.fat.actions.RunWithMpJwtVersion;
import com.ibm.ws.security.jwtsso.fat.expectations.CookieExpectation;
import com.ibm.ws.security.jwtsso.fat.utils.CommonExpectations;
import com.ibm.ws.security.jwtsso.fat.utils.JwtFatConstants;
import com.ibm.ws.security.jwtsso.fat.utils.JwtFatUtils;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.custom.junit.runner.RepeatTestFilter;
import componenttest.rules.repeater.RepeatTests;
import componenttest.topology.impl.LibertyServer;

@RunWith(FATRunner.class)
public class CookieProcessingTests extends CommonSecurityFat {

    protected static Class<?> thisClass = CookieProcessingTests.class;

    // some common params used my multiple tests
    Page response = null;
    Expectations expectations = null;
    WebClient wc = null;
    String protectedUrl = "http://" + server.getHostname() + ":" + server.getHttpDefaultPort() + "/formlogin/SimpleServlet";
    String defaultUser = JwtFatConstants.TESTUSER;
    String defaultPassword = JwtFatConstants.TESTUSERPWD;

    @ClassRule
    public static RepeatTests r = RepeatTests.with(new RunWithMpJwtVersion(JwtFatConstants.NO_MPJWT))
                    .andWith(new RunWithMpJwtVersion(JwtFatConstants.MPJWT_VERSION_11))
                    .andWith(new RunWithMpJwtVersion(JwtFatConstants.MPJWT_VERSION_12))
                    .andWith(new RunWithMpJwtVersion(JwtFatConstants.MPJWT_VERSION_20))
                    .andWith(new RunWithMpJwtVersion(JwtFatConstants.NO_MPJWT_EE9));

    @Server("com.ibm.ws.security.jwtsso.fat")
    public static LibertyServer server;

    private final JwtFatActions actions = new JwtFatActions();
    private final TestValidationUtils validationUtils = new TestValidationUtils();
    private static JwtFatUtils fatUtils = new JwtFatUtils();

    @BeforeClass
    public static void setUp() throws Exception {

        fatUtils.updateFeatureFile(server, "jwtSsoFeatures", RepeatTestFilter.getMostRecentRepeatAction());

        FATSuite.transformApps(server, "apps/formlogin.war", "dropins/testmarker.war");
        server.addInstalledAppForValidation(JwtFatConstants.APP_FORMLOGIN);
        serverTracker.addServer(server);
        server.startServerUsingExpandedConfiguration("server_withFeature.xml", CommonWaitForAppChecks.getLTPAReadyMsgs(CommonWaitForAppChecks.getSSLChannelReadyMsgs()));

    }

    /**
     * perform the happy path login and access of protected resource.
     *
     * @throws Exception
     */
    void doHappyPath() throws Exception {
        wc = new WebClient();

        String currentAction = TestActions.ACTION_INVOKE_PROTECTED_RESOURCE;
        expectations = new Expectations();
        expectations.addExpectations(CommonExpectations.successfullyReachedLoginPage(currentAction));
        response = actions.invokeUrl(_testName, wc, protectedUrl); // get back the login page
        validationUtils.validateResult(response, currentAction, expectations);

        currentAction = TestActions.ACTION_SUBMIT_LOGIN_CREDENTIALS;
        expectations.addExpectations(CommonExpectations.successfullyReachedUrl(currentAction, protectedUrl));
        expectations.addExpectation(new CookieExpectation(currentAction, wc, JwtFatConstants.JWT_COOKIE_NAME, ".+", JwtFatConstants.NOT_SECURE, JwtFatConstants.HTTPONLY));
        expectations.addExpectations(CommonExpectations.getResponseTextExpectationsForJwtCookie(currentAction, JwtFatConstants.JWT_COOKIE_NAME, defaultUser));
        expectations.addExpectations(CommonExpectations.getJwtPrincipalExpectations(currentAction, defaultUser, JwtFatConstants.DEFAULT_ISS_REGEX));

        response = actions.doFormLogin(response, JwtFatConstants.TESTUSER, JwtFatConstants.TESTUSERPWD);
        // confirm protected resource was accessed
        validationUtils.validateResult(response, currentAction, expectations);
    }

    /**
     * Test the splitting of a very large JWT token into multiple cookies.
     * A contrived group name will be used to construct a very large token.
     * Invoke app on the happy path, check that jwt cookies with correct names came back.
     * Access the app a second time to force the cookies to be merged back into a single token,
     * confirm that access is successful.
     *
     */
    @Mode(TestMode.LITE)
    @Test
    public void test_largeCookies() throws Exception {
        server.reconfigureServerUsingExpandedConfiguration(_testName, "server_testlargecookies.xml");

        doHappyPath();
        // The test app logs the cookies,  check them that way.  Or we could look at the response headers.
        String responseStr = response.getWebResponse().getContentAsString();
        assertTrue("expected cookie MPJWT    not found in cookies", responseStr.contains(JwtFatConstants.EXPECTED_COOKIE_NAME));
        assertTrue("expected cookie MPJWT02  not found in cookies", responseStr.contains(JwtFatConstants.EXPECTED_COOKIE_2_NAME));

        // now access resource a second time, force cookies to be rejoined into a single token
        response = actions.invokeUrl(_testName, wc, protectedUrl);
        responseStr = response.getWebResponse().getContentAsString();
        boolean check2 = responseStr.contains("SimpleServlet");
        assertTrue("Did not successfully access the protected resource a second time", check2);

    }

    /**
     * check for presence of cleared cookies in the response headers
     *
     * @param lookForSecondCookie
     */
    void confirmCookiesCleared(boolean lookForSecondCookie) {
        //  look through all the headers as >1 Set-Cookie header might be sent back.
        List<NameValuePair> headerList = response.getWebResponse().getResponseHeaders();
        Iterator it = headerList.iterator();
        String combinedCookieValues = null;
        while (it.hasNext()) {
            NameValuePair nvp = (NameValuePair) it.next();
            if (nvp.getName().equals("Set-Cookie")) {
                combinedCookieValues += (nvp.getValue() + " ");
            }

        }
        //String cookie = response.getWebResponse().getResponseHeaderValue("Set-Cookie");
        Log.info(thisClass, "", "value of combined cookie header values: " + combinedCookieValues);
        assertTrue("did not find expected  cookie", combinedCookieValues != null);
        assertTrue("cookie name is wrong", combinedCookieValues.contains(JwtFatConstants.JWT_COOKIE_NAME));

        assertTrue("cookie MPJWT is not cleared", combinedCookieValues.contains(JwtFatConstants.JWT_COOKIE_NAME + "=\"\";"));
        if (lookForSecondCookie) {
            assertTrue("cookie MPJWT02 is not cleared", combinedCookieValues.contains(JwtFatConstants.JWT_COOKIE_NAME + "02=\"\";"));
        }
    }

    /**
     * Test that after accessing a protected resource and getting back a token, a call to HttpServletRequest.logout()
     * clears all jwt cookies
     */
    @Mode(TestMode.LITE)
    @Test
    public void test_ServletLogout() throws Exception {
        server.reconfigureServerUsingExpandedConfiguration(_testName, "server_testlargecookies.xml");
        doHappyPath();

        // add attribute to tell the app to logout
        String logoutUrl = protectedUrl + "?logout=true";
        // now access resource a second time, force cookies to be rejoined into a single token
        response = actions.invokeUrl(_testName, wc, logoutUrl);
        String responseStr = response.getWebResponse().getContentAsString();
        boolean check2 = responseStr.contains("Test Application class BaseServlet logged out");
        assertTrue("Did not get a response indicating logout was invoked", check2);
        confirmCookiesCleared(true);

        // and make sure we cannot access protected resource
        response = actions.invokeUrl(_testName, wc, protectedUrl);
        responseStr = response.getWebResponse().getContentAsString();
        assertFalse("should not have been able to access protected url ", responseStr.contains("SimpleServlet"));
    }

    /**
     * Test that after accessing a protected resource and getting back a token, a call to the ibm_security_logout
     * url clears all jwt cookies
     */
    @Mode(TestMode.LITE)
    @Test
    public void test_ibm_security_logout() throws Exception {
        server.reconfigureServerUsingExpandedConfiguration(_testName, "server_testlargecookies.xml");
        doHappyPath();
        // add attribute to tell the app to logout
        String logoutUrl = protectedUrl.replace("SimpleServlet", "ibm_security_logout");
        // now access resource a second time, force cookies to be rejoined into a single token
        WebRequest request = new WebRequest(new URL(logoutUrl), HttpMethod.POST);
        response = actions.submitRequest(_testName, wc, request);
        confirmCookiesCleared(true);

        // and make sure we cannot access protected resource
        response = actions.invokeUrl(_testName, wc, protectedUrl);
        String responseStr = response.getWebResponse().getContentAsString();
        assertFalse("should not have been able to access protected url ", responseStr.contains("SimpleServlet"));
    }

    /**
     * Test that after accessing a protected resource and then logging out,
     * Replaying the jwttoken is detected and access is denied.
     */
    @Mode(TestMode.LITE)
    @Test
    public void test_CookieReplay() throws Exception {

        doHappyPath();
        String responseStr = response.getWebResponse().getContentAsString();
        String beginStr = "cookie: " + JwtFatConstants.JWT_COOKIE_NAME + " value: ";
        int begin = responseStr.indexOf(beginStr) + beginStr.length();
        int end = responseStr.indexOf("\n", begin);
        String token = responseStr.substring(begin, end);
        Log.info(thisClass, "", "value of cookie from response text " + token);
        assertTrue("did not find expected  cookie", token != null);

        // perform logout
        String logoutUrl = protectedUrl + "?logout=true";
        response = actions.invokeUrl(_testName, wc, logoutUrl);
        responseStr = response.getWebResponse().getContentAsString();
        boolean check2 = responseStr.contains("Test Application class BaseServlet logged out");
        assertTrue("Did not get a response indicating logout was invoked", check2);

        // attempt the replay.
        String thisMethod = "test_CookieReplay";
        loggingUtils.printMethodName(thisMethod);

        WebRequest request = new WebRequest(new URL(protectedUrl), HttpMethod.GET);
        Log.info(thisClass, "", "setting cookie for replay:" + token);
        request.setAdditionalHeader("Cookie", JwtFatConstants.JWT_COOKIE_NAME + "=" + token);
        loggingUtils.printRequestParts(wc, request, _testName);

        Page response = wc.getPage(request); // should get bounced to login page
        loggingUtils.printResponseParts(response, _testName, "Response from URL: ");

        boolean accessedResource = response.getWebResponse().getContentAsString().contains("SimpleServlet");
        assertFalse("should not have been able to access the protected resource", accessedResource);

        // CWWKS9126A: Authentication using a JSON Web Token did not succeed because the token was previously logged out.
        String errorMsg = server.waitForStringInLogUsingMark("CWWKS9126A", 100);
        assertFalse("Did not find expected replay warning message CWWKS9126A in log", errorMsg == null);
    }

    /**
     * Test that when a JWT token is sent in the auth header instead of in a cookie,
     * we accept it same as if we sent a cookie.
     *
     * Since the option to respect the type of application_auth is not specified
     * in the mpJwt configuration, any application will be accessed directly
     * without going through login page, basic challenge, etc.
     *
     * @throws Exception
     */
    @Mode(TestMode.LITE)
    @Test
    public void test_TokenInAuthHeader() throws Exception {

        // we need to make sure we get a jwt with a unique issued-at claim, i.e. at least one second after last test,
        // otherwise we can get a duplicate jwt, same as prior test, and test will fail because jwt is a duplicate.
        Thread.sleep(1100);

        // get jwt token from token endpoint
        String tokenEndpointUrl = "https://" + server.getHostname() + ":" + server.getHttpDefaultSecurePort() + "/jwt/ibm/api/defaultJwtSso/token";
        wc = actions.createWebClient();
        response = actions.invokeUrlWithBasicAuth(_testName, wc, tokenEndpointUrl, defaultUser, defaultPassword);
        String responseStr = response.getWebResponse().getContentAsString();
        Log.info(thisClass, _testName, "received this from token endpoint: " + responseStr);
        String token = FatStringUtils.extractRegexGroup(responseStr, "\"token\": \"(.+)\"");
        Log.info(thisClass, _testName, "parsed token: " + token);

        String action = JwtFatActions.ACTION_INVOKE_PROTECTED_RESOURCE;

        wc = actions.createWebClient();
        response = actions.invokeUrlWithBearerToken(_testName, wc, protectedUrl, token);

        Expectations expectations = new Expectations();
        expectations.addExpectations(CommonExpectations.successfullyReachedUrl(action, protectedUrl));
        expectations.addExpectations(CommonExpectations.getJwtPrincipalExpectations(action, defaultUser, JwtFatConstants.DEFAULT_ISS_REGEX));
        expectations.addExpectations(CommonExpectations.cookieDoesNotExist(action, wc, JwtFatConstants.JWT_COOKIE_NAME));
        validationUtils.validateResult(response, action, expectations);
        actions.destroyWebClient(wc);

    }

    //TODO: more tests for multiple cookies on non-root path

}
