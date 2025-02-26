/*******************************************************************************
 * Copyright (c) 2018, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.jwt.fat.builder;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.Page;
import com.ibm.json.java.JSONObject;
import com.ibm.ws.security.fat.common.CommonSecurityFat;
import com.ibm.ws.security.fat.common.expectations.Expectations;
import com.ibm.ws.security.fat.common.jwt.HeaderConstants;
import com.ibm.ws.security.fat.common.jwt.PayloadConstants;
import com.ibm.ws.security.fat.common.utils.SecurityFatHttpUtils;
import com.ibm.ws.security.fat.common.validation.TestValidationUtils;
import com.ibm.ws.security.jwt.fat.builder.actions.JwtBuilderActions;
import com.ibm.ws.security.jwt.fat.builder.utils.BuilderHelpers;
import com.ibm.ws.security.jwt.fat.builder.utils.JwtBuilderMessageConstants;

import componenttest.annotation.AllowedFFDC;
import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

/**
 * Tests that use the Consumer API when extending the ConsumeMangledJWTTests.
 * The server will be configured with the appropriate jwtConsumer's
 * We will validate that we can <use> (and the output is correct):
 * 1) create a JWTConsumer
 * 2) create a JwtToken object
 * 3) create a claims object
 * 4) use all of the get methods on the claims object
 * 5) use toJsonString method got get all attributes in the payload
 *
 */

@Mode(TestMode.FULL)
@RunWith(FATRunner.class)
@AllowedFFDC({ "org.apache.http.NoHttpResponseException" })
public class JwtBuilderAPIMinimumRunnableConfigTests extends CommonSecurityFat {

    @Server("com.ibm.ws.security.jwt_fat.builder")
    public static LibertyServer builderServer;

    private static final JwtBuilderActions actions = new JwtBuilderActions();
    public static final TestValidationUtils validationUtils = new TestValidationUtils();

    @BeforeClass
    public static void setUp() throws Exception {
        transformApps(builderServer);

        serverTracker.addServer(builderServer);
        skipRestoreServerTracker.addServer(builderServer);
        builderServer.addInstalledAppForValidation(JWTBuilderConstants.JWT_BUILDER_SERVLET);
        builderServer.startServerUsingExpandedConfiguration("server_minimumRunnableConfig.xml");
        SecurityFatHttpUtils.saveServerPorts(builderServer, JWTBuilderConstants.BVT_SERVER_1_PORT_NAME_ROOT);

        // the server's default config contains an invalid value (on purpose), tell the fat framework to ignore it!
        builderServer.addIgnoredErrors(Arrays.asList(JwtBuilderMessageConstants.CWWKG0032W_CONFIG_INVALID_VALUE));

    }

    /**
     * <p>
     * Test Purpose:
     * </p>
     * <OL>
     * <LI>Invoke the JWT Builder using the default builder config.
     * <LI>What this means is that we're not specifying any JWT Builder config, therefore, we'll use attributes from base config
     * as well as embedded defaults
     * </OL>
     * <P>
     * Expected Results:
     * <OL>
     * <LI>Should get a token built using the default values for the JWT Token
     * </OL>
     */
    @Test
    public void JwtBuilderAPIMinimumConfigTests_minimumRunnableConfig() throws Exception {

        JSONObject expectationSettings = BuilderHelpers.setDefaultClaims(builderServer);
        expectationSettings.put(PayloadConstants.ISSUER, SecurityFatHttpUtils.getServerIpUrlBase(builderServer) + "jwt/defaultJWT");
        JSONObject testSettings = new JSONObject();
        testSettings.put(HeaderConstants.ALGORITHM, JWTBuilderConstants.SIGALG_RS256);
        testSettings.put(JWTBuilderConstants.SHARED_KEY_TYPE, JWTBuilderConstants.SHARED_KEY_PRIVATE_KEY_TYPE);
        expectationSettings.put("overrideSettings", testSettings);

        Expectations expectations = BuilderHelpers.createGoodBuilderExpectations(JWTBuilderConstants.JWT_BUILDER_SETAPIS_ENDPOINT, expectationSettings, builderServer);

        Page response = actions.invokeJwtBuilder_setApis(_testName, builderServer, null, testSettings);
        validationUtils.validateResult(response, expectations);

    }

}
