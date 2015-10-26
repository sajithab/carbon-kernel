/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.launcher.test;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.launcher.Constants;
import org.wso2.carbon.launcher.bootstrap.logging.BootstrapLogger;
import org.wso2.carbon.launcher.config.CarbonInitialBundle;
import org.wso2.carbon.launcher.config.CarbonLaunchConfig;
import org.wso2.carbon.launcher.utils.Utils;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.wso2.carbon.launcher.Constants.DEFAULT_PROFILE;
import static org.wso2.carbon.launcher.Constants.LAUNCH_PROPERTIES_FILE;
import static org.wso2.carbon.launcher.Constants.LOG_LEVEL_WARN;
import static org.wso2.carbon.launcher.Constants.PAX_DEFAULT_SERVICE_LOG_LEVEL;
import static org.wso2.carbon.launcher.Constants.PROFILE;

/**
 * Test loading launch configurations from launch.properties file
 */
public class LoadLaunchConfigTest extends BaseTest {
    private Logger logger;
    private CarbonLaunchConfig launchConfig;

    public LoadLaunchConfigTest() {
        super();
    }

    @BeforeClass
    public void init() {
        //setting carbon.home system property to test/resources location
        System.setProperty(Constants.CARBON_HOME, testResourceDir);
        logger = BootstrapLogger.getCarbonLogger(LoadLaunchConfigTest.class.getName());

        String profileName = System.getProperty(PROFILE);
        if (profileName == null || profileName.length() == 0) {
            System.setProperty(PROFILE, DEFAULT_PROFILE);
        }

        // Set log level for Pax logger to WARN.
        System.setProperty(PAX_DEFAULT_SERVICE_LOG_LEVEL, LOG_LEVEL_WARN);
    }

    @Test
    public void loadCarbonLaunchConfigTestCase() {
        String launchPropFilePath = Paths.get(Utils.getLaunchConfigDirectory().toString(),
                LAUNCH_PROPERTIES_FILE).toString();
        File launchPropFile = new File(launchPropFilePath);

        if (launchPropFile.exists()) {
            logger.log(Level.FINE, "Loading the Carbon launch configuration from the file " +
                    launchPropFile.getAbsolutePath());

            //loading launch.properties file
            launchConfig = new CarbonLaunchConfig(launchPropFile);
        }
        Assert.assertTrue(launchPropFile.exists(), "launch.properties file does not exists");
    }

    @Test(dependsOnMethods = {"loadCarbonLaunchConfigTestCase"})
    public void loadLaunchConfigOSGiFrameworkTestCase() {
        //test if property "carbon.osgi.framework" has set according to sample launch.properties file
        URL url = launchConfig.getCarbonOSGiFramework();
        Assert.assertEquals(url.getFile().split("plugins")[1],
                "/org.eclipse.osgi_3.10.2.v20150203-1939.jar");
    }

    @Test(dependsOnMethods = {"loadCarbonLaunchConfigTestCase"})
    public void loadLaunchConfigInitialBundlesTestCase() {
        //test if property "carbon.initial.osgi.bundles" has set according to sample launch.properties file
        List<CarbonInitialBundle> initialBundleList = launchConfig.getInitialBundles();
        Assert.assertEquals(initialBundleList.get(0).getLocation().getFile().split("plugins")[1],
                "/org.eclipse.equinox.simpleconfigurator_1.1.0.v20131217-1203.jar");
    }
}
