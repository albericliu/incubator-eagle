/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.apache.eagle.jpm.mr.running.storm;

import org.apache.eagle.jpm.mr.running.MRRunningJobConfig;
import org.apache.eagle.jpm.mr.running.recover.MRRunningJobManager;
import org.apache.eagle.jpm.mr.runningentity.JobExecutionAPIEntity;
import org.apache.eagle.jpm.util.Constants;
import org.apache.eagle.jpm.util.Utils;
import org.apache.eagle.jpm.util.resourcefetch.RMResourceFetcher;
import org.apache.eagle.jpm.util.resourcefetch.ResourceFetcher;
import org.apache.eagle.jpm.util.resourcefetch.model.AppInfo;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MRRunningJobFetchSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(MRRunningJobFetchSpout.class);
    private MRRunningJobConfig.JobExtractorConfig jobExtractorConfig;
    private MRRunningJobConfig.EndpointConfig endpointConfig;
    private MRRunningJobConfig.ZKStateConfig zkStateConfig;
    private ResourceFetcher resourceFetcher;
    private SpoutOutputCollector collector;
    private boolean init;
    private transient MRRunningJobManager runningJobManager;
    private Set<String> runningYarnApps;

    public MRRunningJobFetchSpout(MRRunningJobConfig.JobExtractorConfig jobExtractorConfig,
                                  MRRunningJobConfig.EndpointConfig endpointConfig,
                                  MRRunningJobConfig.ZKStateConfig zkStateConfig) {
        this.jobExtractorConfig = jobExtractorConfig;
        this.endpointConfig = endpointConfig;
        this.zkStateConfig = zkStateConfig;
        this.init = false;
        this.runningYarnApps = new HashSet<>();
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        resourceFetcher = new RMResourceFetcher(endpointConfig.rmUrls);
        collector = spoutOutputCollector;
        this.runningJobManager = new MRRunningJobManager(zkStateConfig);
    }

    @Override
    public void nextTuple() {
        LOG.info("Start to fetch mr running jobs");
        try {
            List<AppInfo> apps;
            Map<String, Map<String, JobExecutionAPIEntity>> mrApps = null;
            if (!this.init) {
                mrApps = recoverRunningApps();

                apps = new ArrayList<>();
                for (String appId : mrApps.keySet()) {
                    Map<String, JobExecutionAPIEntity> jobs = mrApps.get(appId);
                    if (jobs.size() > 0) {
                        Set<String> jobIds = jobs.keySet();
                        apps.add(jobs.get(jobIds.iterator().next()).getAppInfo());
                        this.runningYarnApps.add(appId);
                    }
                }
                LOG.info("recover {} mr yarn apps from zookeeper", apps.size());
                this.init = true;
            } else {
                apps = resourceFetcher.getResource(Constants.ResourceType.RUNNING_MR_JOB);
                LOG.info("get {} apps from resource manager", apps.size());
                Set<String> running = new HashSet<>();
                Iterator<String> appIdIterator = this.runningYarnApps.iterator();
                while (appIdIterator.hasNext()) {
                    String appId = appIdIterator.next();
                    boolean hasFinished = true;
                    for (AppInfo appInfo : apps) {
                        if (appId.equals(appInfo.getId())) {
                            hasFinished = false;
                        }
                        running.add(appInfo.getId());
                    }

                    if (hasFinished) {
                        try {
                            Map<String, JobExecutionAPIEntity> result = this.runningJobManager.recoverYarnApp(appId);
                            if (result.size() > 0) {
                                if (mrApps == null) {
                                    mrApps = new HashMap<>();
                                }
                                mrApps.put(appId, result);
                                AppInfo appInfo = result.get(result.keySet().iterator().next()).getAppInfo();
                                appInfo.setState(Constants.AppState.FINISHED.toString());
                                apps.add(appInfo);
                            }
                        } catch (KeeperException.NoNodeException e) {
                            LOG.warn("{}", e);
                            LOG.warn("yarn app {} has finished", appId);
                        }
                    }
                }

                this.runningYarnApps = running;
                LOG.info("get {} total apps(contains finished)", apps.size());
            }

            for (int i = 0; i < apps.size(); i++) {
                LOG.info("emit mr yarn application " + apps.get(i).getId());
                AppInfo appInfo = apps.get(i);
                if (mrApps != null && mrApps.containsKey(appInfo.getId())) {
                    //emit (AppInfo, Map<String, JobExecutionAPIEntity>)
                    collector.emit(new Values(appInfo.getId(), appInfo, mrApps.get(appInfo.getId())));
                } else {
                    collector.emit(new Values(appInfo.getId(), appInfo, null));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.sleep(jobExtractorConfig.fetchRunningJobInterval);
        }
    }

    private Map<String, Map<String, JobExecutionAPIEntity>> recoverRunningApps() {
        //we need read from zookeeper, path looks like /apps/mr/running/yarnAppId/jobId/
        //content of path /apps/mr/running/yarnAppId/jobId is JobExecutionAPIEntity
        //as we know, a yarn application may contains many mr jobs
        //so, the returned results is a Map-Map
        //<yarnAppId, <jobId, JobExecutionAPIEntity>>
        Map<String, Map<String, JobExecutionAPIEntity>> result = this.runningJobManager.recover();
        return result;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("appId", "appInfo", "mrJobEntity"));
    }

    @Override
    public void fail(Object msgId) {

    }

    @Override
    public void ack(Object msgId) {

    }

    @Override
    public void close() {
    }
}