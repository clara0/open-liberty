/*******************************************************************************
 * Copyright (c) 2019, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.reactive.messaging.fat.suite;

import java.time.Duration;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.containers.KafkaSaslPlainContainer;
import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.liberty_login.LibertyLoginModuleTest;
import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.liberty_login.invalid.LibertyLoginModuleInvalidTest;
import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.liberty_login.none.LibertyLoginModuleNoEncTest;
import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.liberty_login.special_chars.LibertyLoginModuleSpecialCharsTest;
import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.liberty_login.xor.LibertyLoginModuleXorTest;
import com.ibm.ws.microprofile.reactive.messaging.fat.kafka.sasl_plain.KafkaSaslPlainTest;

import componenttest.containers.ExternalTestServiceDockerClientStrategy;

/**
 * Suite for tests which run against a TLS enabled kafka broker
 */
@RunWith(Suite.class)
@SuiteClasses({
                KafkaSaslPlainTest.class,
                LibertyLoginModuleTest.class,
                LibertyLoginModuleXorTest.class,
                LibertyLoginModuleNoEncTest.class,
                LibertyLoginModuleInvalidTest.class,
                LibertyLoginModuleSpecialCharsTest.class,
})
public class SaslPlainTests {

    //Required to ensure we calculate the correct strategy each run even when
    //switching between local and remote docker hosts.
    static {
        ExternalTestServiceDockerClientStrategy.setupTestcontainers();
    }

    @ClassRule
    public static KafkaSaslPlainContainer kafkaContainer = new KafkaSaslPlainContainer()
                    .withStartupTimeout(Duration.ofMinutes(2))
                    .withStartupAttempts(3);

}
