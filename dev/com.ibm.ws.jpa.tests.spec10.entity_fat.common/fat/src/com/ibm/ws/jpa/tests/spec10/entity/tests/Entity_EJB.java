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
package com.ibm.ws.jpa.tests.spec10.entity.tests;

import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.JdbcDatabaseContainer;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.config.Application;
import com.ibm.websphere.simplicity.config.ServerConfiguration;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.BasicAnnotation_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.DatatypeSupport_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.EmbeddableID_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.Embeddable_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.IDClass_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.MultiTable_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.PKEntity_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.PKGenerator_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.ReadOnly_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.Serializable_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sf.Versioning_EJB_SF_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.BasicAnnotation_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.DatatypeSupport_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.EmbeddableID_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.Embeddable_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.IDClass_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.MultiTable_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.PKEntity_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.PKGenerator_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.ReadOnly_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.Serializable_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sfex.Versioning_EJB_SFEX_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.BasicAnnotation_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.DatatypeSupport_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.EmbeddableID_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.Embeddable_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.IDClass_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.MultiTable_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.PKEntity_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.PKGenerator_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.ReadOnly_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.Serializable_EJB_SL_TestServlet;
import com.ibm.ws.jpa.fvt.entity.tests.ejb.sl.Versioning_EJB_SL_TestServlet;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.annotation.TestServlets;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.database.container.DatabaseContainerType;
import componenttest.topology.database.container.DatabaseContainerUtil;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.PrivHelper;

@RunWith(FATRunner.class)
@Mode(TestMode.FULL)
public class Entity_EJB extends JPAFATServletClient {

    private final static String CONTEXT_ROOT = "Entity10EJB";
    private final static String RESOURCE_ROOT = "test-applications/entity/";
    private final static String appFolder = "ejb";
    private final static String appName = "EntityEJB10";
    private final static String appNameEar = appName + ".ear";

    private final static Set<String> dropSet = new HashSet<String>();
    private final static Set<String> createSet = new HashSet<String>();

    private static long timestart = 0;

    static {
        dropSet.add("JPA10_ENTITY_DROP_${dbvendor}.ddl");
        createSet.add("JPA10_ENTITY_CREATE_${dbvendor}.ddl");
    }

    @Server("JPA10EJBEntityServer")
    @TestServlets({
                    @TestServlet(servlet = BasicAnnotation_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "BasicAnnotation_EJB_SL_TestServlet"),
                    @TestServlet(servlet = DatatypeSupport_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "DatatypeSupport_EJB_SL_TestServlet"),
                    @TestServlet(servlet = EmbeddableID_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "EmbeddableID_EJB_SL_TestServlet"),
                    @TestServlet(servlet = Embeddable_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "Embeddable_EJB_SL_TestServlet"),
                    @TestServlet(servlet = IDClass_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "IDClass_EJB_SL_TestServlet"),
                    @TestServlet(servlet = MultiTable_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "MultiTable_EJB_SL_TestServlet"),
                    @TestServlet(servlet = PKEntity_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "PKEntity_EJB_SL_TestServlet"),
                    @TestServlet(servlet = PKGenerator_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "PKGenerator_EJB_SL_TestServlet"),
                    @TestServlet(servlet = ReadOnly_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "ReadOnly_EJB_SL_TestServlet"),
                    @TestServlet(servlet = Serializable_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "Serializable_EJB_SL_TestServlet"),
                    @TestServlet(servlet = Versioning_EJB_SL_TestServlet.class, path = CONTEXT_ROOT + "/" + "Versioning_EJB_SL_TestServlet"),

                    @TestServlet(servlet = BasicAnnotation_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "BasicAnnotation_EJB_SF_TestServlet"),
                    @TestServlet(servlet = DatatypeSupport_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "DatatypeSupport_EJB_SF_TestServlet"),
                    @TestServlet(servlet = EmbeddableID_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "EmbeddableID_EJB_SF_TestServlet"),
                    @TestServlet(servlet = Embeddable_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "Embeddable_EJB_SF_TestServlet"),
                    @TestServlet(servlet = IDClass_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "IDClass_EJB_SF_TestServlet"),
                    @TestServlet(servlet = MultiTable_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "MultiTable_EJB_SF_TestServlet"),
                    @TestServlet(servlet = PKEntity_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "PKEntity_EJB_SF_TestServlet"),
                    @TestServlet(servlet = PKGenerator_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "PKGenerator_EJB_SF_TestServlet"),
                    @TestServlet(servlet = ReadOnly_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "ReadOnly_EJB_SF_TestServlet"),
                    @TestServlet(servlet = Serializable_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "Serializable_EJB_SF_TestServlet"),
                    @TestServlet(servlet = Versioning_EJB_SF_TestServlet.class, path = CONTEXT_ROOT + "/" + "Versioning_EJB_SF_TestServlet"),

                    @TestServlet(servlet = BasicAnnotation_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "BasicAnnotation_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = DatatypeSupport_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "DatatypeSupport_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = EmbeddableID_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "EmbeddableID_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = Embeddable_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "Embeddable_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = IDClass_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "IDClass_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = MultiTable_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "MultiTable_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = PKEntity_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "PKEntity_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = PKGenerator_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "PKGenerator_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = ReadOnly_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "ReadOnly_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = Serializable_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "Serializable_EJB_SFEX_TestServlet"),
                    @TestServlet(servlet = Versioning_EJB_SFEX_TestServlet.class, path = CONTEXT_ROOT + "/" + "Versioning_EJB_SFEX_TestServlet"),

    //
    })
    public static LibertyServer server;

    public static final JdbcDatabaseContainer<?> testContainer = AbstractFATSuite.testContainer;

    @BeforeClass
    public static void setUp() throws Exception {
        PrivHelper.generateCustomPolicy(server, AbstractFATSuite.JAXB_PERMS);
        bannerStart(Entity_Web.class);
        timestart = System.currentTimeMillis();

        int appStartTimeout = server.getAppStartTimeout();
        if (appStartTimeout < (120 * 1000)) {
            server.setAppStartTimeout(120 * 1000);
        }

        int configUpdateTimeout = server.getConfigUpdateTimeout();
        if (configUpdateTimeout < (120 * 1000)) {
            server.setConfigUpdateTimeout(120 * 1000);
        }

        //Get driver name
        server.addEnvVar("DB_DRIVER", DatabaseContainerType.valueOf(testContainer).getDriverName());

        //Setup server DataSource properties
        DatabaseContainerUtil.setupDataSourceProperties(server, testContainer);

        server.startServer();

        setupDatabaseApplication(server, RESOURCE_ROOT + "ddl/");

        final Set<String> ddlSet = new HashSet<String>();

        System.out.println(Entity_Web.class.getName() + " Setting up database tables...");

        ddlSet.clear();
        for (String ddlName : dropSet) {
            ddlSet.add(ddlName.replace("${dbvendor}", getDbVendor().name()));
        }
        executeDDL(server, ddlSet, true);

        ddlSet.clear();
        for (String ddlName : createSet) {
            ddlSet.add(ddlName.replace("${dbvendor}", getDbVendor().name()));
        }
        executeDDL(server, ddlSet, false);

        setupTestApplication();
    }

    private static void setupTestApplication() throws Exception {
        JavaArchive ejbApp = ShrinkWrap.create(JavaArchive.class, appName + ".jar");
        ejbApp.addPackages(true, "com.ibm.ws.jpa.fvt.entity.ejblocal");
        ejbApp.addPackages(true, "com.ibm.ws.jpa.fvt.entity.entities");
        ejbApp.addPackages(true, "com.ibm.ws.jpa.fvt.entity.support");
        ejbApp.addPackages(true, "com.ibm.ws.jpa.fvt.entity.testlogic");
        ShrinkHelper.addDirectory(ejbApp, RESOURCE_ROOT + appFolder + "/" + appName + ".jar");

        WebArchive webApp = ShrinkWrap.create(WebArchive.class, appName + ".war");
        webApp.addPackages(true, "com.ibm.ws.jpa.fvt.entity.tests.ejb");
        ShrinkHelper.addDirectory(webApp, RESOURCE_ROOT + appFolder + "/" + appName + ".war");

        final JavaArchive testApiJar = buildTestAPIJar();

        final EnterpriseArchive app = ShrinkWrap.create(EnterpriseArchive.class, appNameEar);
        app.addAsModule(ejbApp);
        app.addAsModule(webApp);
        app.addAsLibrary(testApiJar);
        ShrinkHelper.addDirectory(app, RESOURCE_ROOT + appFolder, new org.jboss.shrinkwrap.api.Filter<ArchivePath>() {

            @Override
            public boolean include(ArchivePath arg0) {
                if (arg0.get().startsWith("/META-INF/")) {
                    return true;
                }
                return false;
            }

        });

        ShrinkHelper.exportToServer(server, "apps", app);

        Application appRecord = new Application();
        appRecord.setLocation(appNameEar);
        appRecord.setName(appName);

        server.setMarkToEndOfLog();
        ServerConfiguration sc = server.getServerConfiguration();
        sc.getApplications().add(appRecord);
        server.updateServerConfiguration(sc);
        server.saveServerConfiguration();

        HashSet<String> appNamesSet = new HashSet<String>();
        appNamesSet.add(appName);
        server.waitForConfigUpdateInLogUsingMark(appNamesSet, "");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            // Clean up database
            try {
                final Set<String> ddlSet = new HashSet<String>();
                for (String ddlName : dropSet) {
                    ddlSet.add(ddlName.replace("${dbvendor}", getDbVendor().name()));
                }
                executeDDL(server, ddlSet, true);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            server.stopServer("CWWJP9991W", // From Eclipselink drop-and-create tables option
                              "WTRN0074E: Exception caught from before_completion synchronization operation" // RuntimeException test, expected
            );
        } finally {
            try {
                ServerConfiguration sc = server.getServerConfiguration();
                sc.getApplications().clear();
                server.updateServerConfiguration(sc);
                server.saveServerConfiguration();

                server.deleteFileFromLibertyServerRoot("apps/" + appNameEar);
                server.deleteFileFromLibertyServerRoot("apps/DatabaseManagement.war");
            } catch (Throwable t) {
                t.printStackTrace();
            }
            bannerEnd(Entity_Web.class, timestart);
        }
    }
}
