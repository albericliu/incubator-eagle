/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.app.jpm;

import org.apache.eagle.app.resource.ApplicationResource;
import org.apache.eagle.app.service.ApplicationOperations;
import org.apache.eagle.app.test.ApplicationTestBase;
import org.apache.eagle.metadata.model.ApplicationEntity;
import org.apache.eagle.metadata.model.SiteEntity;
import org.apache.eagle.metadata.resource.SiteResource;

import com.google.inject.Inject;
import org.apache.eagle.metadata.service.ApplicationStatusUpdateService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JPMWebApplicationTest extends ApplicationTestBase {
    @Inject
    private SiteResource siteResource;

    @Inject
    private ApplicationResource applicationResource;

    @Inject
    private ApplicationStatusUpdateService statusUpdateService;

    private void installDependencies(){
        ApplicationOperations.InstallOperation installDependency1 = new ApplicationOperations.InstallOperation("test_site", "MR_RUNNING_JOB_APP", ApplicationEntity.Mode.LOCAL);
        installDependency1.setConfiguration(getConf());
        applicationResource.installApplication(installDependency1).getData();

        ApplicationOperations.InstallOperation installDependency2 = new ApplicationOperations.InstallOperation("test_site", "MR_HISTORY_JOB_APP", ApplicationEntity.Mode.LOCAL);
        installDependency2.setConfiguration(getConf());
        applicationResource.installApplication(installDependency2).getData();
    }

    /**
     * register site
     * install app
     * start app
     * stop app
     * uninstall app
     *
     * @throws InterruptedException
     */
    @Test
    public void testApplicationLifecycle() throws InterruptedException {
        // Create local site
        SiteEntity siteEntity = new SiteEntity();
        siteEntity.setSiteId("test_site");
        siteEntity.setSiteName("Test Site");
        siteEntity.setDescription("Test Site for JPMApplication");
        siteResource.createSite(siteEntity);
        Assert.assertNotNull(siteEntity.getUuid());

        installDependencies();

        ApplicationOperations.InstallOperation installOperation = new ApplicationOperations.InstallOperation("test_site", "JPM_WEB_APP", ApplicationEntity.Mode.LOCAL);
        installOperation.setConfiguration(getConf());

        // Install application
        ApplicationEntity applicationEntity = applicationResource.installApplication(installOperation).getData();
        //Todo: comment these for now, because they haven't been implemented
        // Start application
//        applicationResource.startApplication(new ApplicationOperations.StartOperation(applicationEntity.getUuid()));
//        // Stop application
//        applicationResource.stopApplication(new ApplicationOperations.StopOperation(applicationEntity.getUuid()));
        //Uninstall application
        applicationResource.uninstallApplication(new ApplicationOperations.UninstallOperation(applicationEntity.getUuid()));
        try {
            applicationResource.getApplicationEntityByUUID(applicationEntity.getUuid());
            Assert.fail("Application instance (UUID: " + applicationEntity.getUuid() + ") should have been uninstalled");
        } catch (Exception ex) {
            // Expected exception
        }
    }

    private Map<String, Object> getConf() {
        Map<String, Object> conf = new HashMap<>();
        conf.put("dataSinkConfig.topic", "testTopic");
        conf.put("dataSinkConfig.brokerList", "broker");
        conf.put("dataSinkConfig.serializerClass", "serializerClass");
        conf.put("dataSinkConfig.keySerializerClass", "keySerializerClass");
        conf.put("spoutNum", 2);
        conf.put("mode", "LOCAL");
        return conf;
    }
}
