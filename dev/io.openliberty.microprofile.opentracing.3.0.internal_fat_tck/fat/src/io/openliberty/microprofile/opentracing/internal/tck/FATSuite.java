/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.microprofile.opentracing.internal.tck;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import componenttest.rules.repeater.FeatureReplacementAction;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * <p>The open tracing FAT suite.</p>
 *
 * <p>This class *must* be named "FATSuite", since the test code is hard coded
 * to look for just that class.</p>
 */
@RunWith(Suite.class)
@SuiteClasses({
    OpentracingTCKLauncher.class,
    OpentracingTCKLauncherMicroProfile.class,
    OpentracingRestClientTCKLauncher.class
})
public class FATSuite {
    private static final String FEATURE_NAME = "io.openliberty.opentracing.mock-3.0.mf";
    private static final String BUNDLE_NAME = "io.openliberty.opentracing.mock-3.0.jar";

    @BeforeClass
    public static void setUp() throws Exception {
        LibertyServer server = LibertyServerFactory.getLibertyServer("OpentracingTCKServer");
        server.copyFileToLibertyInstallRoot("usr/extension/lib/features/", "features/" + FEATURE_NAME);
        server.copyFileToLibertyInstallRoot("usr/extension/lib/", "bundles/" + BUNDLE_NAME);
    }
}
