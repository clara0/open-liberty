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
package com.ibm.ws.testcontainers.example;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import componenttest.containers.ExternalTestServiceDockerClientStrategy;
import componenttest.rules.repeater.FeatureReplacementAction;
import componenttest.rules.repeater.RepeatTests;

@RunWith(Suite.class)
@SuiteClasses({
                ContainersTest.class,       //LITE
                DatabaseRotationTest.class, //LITE
                DockerfileTest.class,       //FULL
                ProgrammaticImageTest.class, //FULL
})
/**
 * Example FATSuite class to show how to setup suite level testcontainers and properties
 */
public class FATSuite {

    static {
        /*
         * By default the testcontainers component and dependencies will log at the following levels:
         * - org.testcontainers: INFO
         * - com.github.dockerjava: WARN
         * - OTHER: ERROR
         *
         * You can overwrite these default configurations programmatically here.
         * This can help in debugging build errors related to testcontainers and docker-java.
         * However, this is not required for successful use of testcontainers.
         */
        Logger root = (Logger) LoggerFactory.getLogger("org.testcontainers");
        root.setLevel(Level.ALL);

        /*
        * THIS METHOD CALL IS REQUIRED TO USE TESTCONTAINERS PLEASE READ:
        *
        * Testcontainers caches data in a properties file located at $HOME/.testcontainers.properties
        * The ExternalTestServiceDockerClientStrategy.setup* methods will clear and reset the values in this property file.
        *
        * By default, testcontainers will attempt to run against a local docker instance and pull from DockerHub.
        * If you want testcontainers to run against a remote docker host to mirror the behavior of an RTC build
        * Then, set property: -Dfat.test.use.remote.docker=true
        * This will only work if you are on the IBM network.
        *
        * We will set the following properties:
        * 1. docker.client.strategy:
        * Default: [Depends on local OS]
        * Custom : componenttest.containers.ExternalTestServiceDockerClientStrategy
        * Purpose: This is the strategy testcontainers uses to locate and run against a remote docker instance.
        *
        * 2. image.substitutor:
        * Default: [none]
        * Custom : componenttest.containers.ArtifactoryImageNameSubstitutor
        * Purpose: This defines a strategy for substituting image names.
        * This is so that we can use a private docker repository to cache docker images
        * to avoid the docker pull limits.
        * Example: foo/bar:1.0 it will get changed to wasliberty-docker-remote.artifactory.swg-devops.com/foo/bar:1.0
         */
        ExternalTestServiceDockerClientStrategy.setupTestcontainers();
    }

    @ClassRule
    public static RepeatTests r = RepeatTests.with(FeatureReplacementAction.NO_REPLACEMENT().fullFATOnly())
                    .andWith(FeatureReplacementAction.EE9_FEATURES());

    /*
     * If you want to use the same container for the entire test suite you can
     * declare it here. Using the @ClassRule annotation will start the container
     * prior to any test classes running.
     *
     * In this example suite I am going to use a different container for each example.
     */
//    @ClassRule
//    public static PostgreSQLContainer container = new PostgreSQLContainer("postgres:9.6.12");

}
