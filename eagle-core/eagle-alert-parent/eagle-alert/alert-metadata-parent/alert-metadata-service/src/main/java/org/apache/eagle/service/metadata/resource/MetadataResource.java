/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.service.metadata.resource;

import org.apache.eagle.alert.coordination.model.Kafka2TupleMetadata;
import org.apache.eagle.alert.coordination.model.ScheduleState;
import org.apache.eagle.alert.coordination.model.internal.PolicyAssignment;
import org.apache.eagle.alert.coordination.model.internal.Topology;
import org.apache.eagle.alert.engine.coordinator.*;
import org.apache.eagle.alert.metadata.IMetadataDao;
import org.apache.eagle.alert.metadata.impl.MetadataDaoFactory;
import org.apache.eagle.alert.metadata.resource.Models;
import org.apache.eagle.alert.metadata.resource.OpResult;
import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.*;

/**
 * @since Apr 11, 2016.
 */
@Path("/metadata")
@Produces("application/json")
@Consumes("application/json")
public class MetadataResource {

    //    private IMetadataDao dao = MetadataDaoFactory.getInstance().getMetadataDao();
    private final IMetadataDao dao;

    public MetadataResource() {
        this.dao = MetadataDaoFactory.getInstance().getMetadataDao();
        ;
    }

    @Inject
    public MetadataResource(IMetadataDao dao) {
        this.dao = dao;
    }

    @Path("/clusters")
    @GET
    public List<StreamingCluster> listClusters() {
        return dao.listClusters();
    }

    @Path("/clear")
    @POST
    public OpResult clear() {
        return dao.clear();
    }

    @Path("/export")
    @POST
    public Models export() {
        return dao.export();
    }

    @Path("/import")
    @POST
    public OpResult importModels(Models model) {
        return dao.importModels(model);
    }

    @Path("/clusters")
    @POST
    public OpResult addCluster(StreamingCluster cluster) {
        return dao.addCluster(cluster);
    }

    @Path("/clusters/batch")
    @POST
    public List<OpResult> addClusters(List<StreamingCluster> clusters) {
        List<OpResult> results = new LinkedList<>();
        for (StreamingCluster cluster : clusters) {
            results.add(dao.addCluster(cluster));
        }
        return results;
    }

    @Path("/clusters/{clusterId}")
    @DELETE
    public OpResult removeCluster(@PathParam("clusterId") String clusterId) {
        return dao.removeCluster(clusterId);
    }

    @Path("/clusters")
    @DELETE
    public List<OpResult> removeClusters(List<String> clusterIds) {
        List<OpResult> results = new LinkedList<>();
        for (String cluster : clusterIds) {
            results.add(dao.removeCluster(cluster));
        }
        return results;
    }

    @Path("/streams")
    @GET
    public List<StreamDefinition> listStreams() {
        return dao.listStreams();
    }

    @Path("/streams")
    @POST
    public OpResult createStream(StreamDefinition stream) {
        return dao.createStream(stream);
    }

    @Path("/streams/batch")
    @POST
    public List<OpResult> addStreams(List<StreamDefinition> streams) {
        List<OpResult> results = new LinkedList<>();
        for (StreamDefinition stream : streams) {
            results.add(dao.createStream(stream));
        }
        return results;
    }

    @Path("/streams/{streamId}")
    @DELETE
    public OpResult removeStream(@PathParam("streamId") String streamId) {
        return dao.removeStream(streamId);
    }

    @Path("/streams")
    @DELETE
    public List<OpResult> removeStreams(List<String> streamIds) {
        List<OpResult> results = new LinkedList<>();
        for (String streamId : streamIds) {
            results.add(dao.removeStream(streamId));
        }
        return results;
    }

    @Path("/datasources")
    @GET
    public List<Kafka2TupleMetadata> listDataSources() {
        return dao.listDataSources();
    }

    @Path("/datasources")
    @POST
    public OpResult addDataSource(Kafka2TupleMetadata dataSource) {
        return dao.addDataSource(dataSource);
    }

    @Path("/datasources/batch")
    @POST
    public List<OpResult> addDataSources(List<Kafka2TupleMetadata> datasources) {
        List<OpResult> results = new LinkedList<>();
        for (Kafka2TupleMetadata ds : datasources) {
            results.add(dao.addDataSource(ds));
        }
        return results;
    }

    @Path("/datasources/{datasourceId}")
    @DELETE
    public OpResult removeDataSource(@PathParam("datasourceId") String datasourceId) {
        return dao.removeDataSource(datasourceId);
    }

    @Path("/datasources")
    @DELETE
    public List<OpResult> removeDataSources(List<String> datasourceIds) {
        List<OpResult> results = new LinkedList<>();
        for (String ds : datasourceIds) {
            results.add(dao.removeDataSource(ds));
        }
        return results;
    }

    @Path("/policies")
    @GET
    public List<PolicyDefinition> listPolicies() {
        return dao.listPolicies();
    }

    @Path("/policies")
    @POST
    public OpResult addPolicy(PolicyDefinition policy) {
        return dao.addPolicy(policy);
    }

    @Path("/policies/batch")
    @POST
    public List<OpResult> addPolicies(List<PolicyDefinition> policies) {
        List<OpResult> results = new LinkedList<>();
        for (PolicyDefinition policy : policies) {
            results.add(dao.addPolicy(policy));
        }
        return results;
    }

    @Path("/policies/{policyId}")
    @DELETE
    public OpResult removePolicy(@PathParam("policyId") String policyId) {
        return dao.removePolicy(policyId);
    }

    @Path("/policies")
    @DELETE
    public List<OpResult> removePolicies(List<String> policies) {
        List<OpResult> results = new LinkedList<>();
        for (String policy : policies) {
            results.add(dao.removePolicy(policy));
        }
        return results;
    }

    @Path("/publishments")
    @GET
    public List<Publishment> listPublishment() {
        return dao.listPublishment();
    }

    @Path("/publishments")
    @POST
    public OpResult addPublishment(Publishment publishment) {
        return dao.addPublishment(publishment);
    }

    @Path("/publishments/batch")
    @POST
    public List<OpResult> addPublishments(List<Publishment> publishments) {
        List<OpResult> results = new LinkedList<>();
        for (Publishment publishment : publishments) {
            results.add(dao.addPublishment(publishment));
        }
        return results;
    }

    @Path("/publishments/{pubId}")
    @DELETE
    public OpResult removePublishment(@PathParam("pubId") String pubId) {
        return dao.removePublishment(pubId);
    }

    @Path("/publishments")
    @DELETE
    public List<OpResult> removePublishments(List<String> pubIds) {
        List<OpResult> results = new LinkedList<>();
        for (String pub : pubIds) {
            results.add(dao.removePublishment(pub));
        }
        return results;
    }

    @Path("/publishmentTypes")
    @GET
    public List<PublishmentType> listPublishmentType() {
        return dao.listPublishmentType();
    }

    @Path("/publishmentTypes")
    @POST
    public OpResult addPublishmentType(PublishmentType publishmentType) {
        return dao.addPublishmentType(publishmentType);
    }

    @Path("/publishmentTypes/batch")
    @POST
    public List<OpResult> addPublishmentTypes(List<PublishmentType> publishmentTypes) {
        List<OpResult> results = new LinkedList<>();
        for (PublishmentType pubType : publishmentTypes) {
            results.add(dao.addPublishmentType(pubType));
        }
        return results;
    }

    @Path("/publishmentTypes/{pubType}")
    @DELETE
    public OpResult removePublishmentType(@PathParam("pubType") String pubType) {
        return dao.removePublishmentType(pubType);
    }

    @Path("/publishmentTypes")
    @DELETE
    public List<OpResult> removePublishmentTypes(List<String> pubTypes) {
        List<OpResult> results = new LinkedList<>();
        for (String pubType : pubTypes) {
            results.add(dao.removePublishmentType(pubType));
        }
        return results;
    }

    @Path("/schedulestates/{versionId}")
    @GET
    public ScheduleState listScheduleState(@PathParam("versionId") String versionId) {
        return dao.getScheduleState(versionId);
    }

    @Path("/schedulestates")
    @GET
    public ScheduleState latestScheduleState() {
        return dao.getScheduleState();
    }

    @Path("/schedulestates")
    @POST
    public OpResult addScheduleState(ScheduleState state) {
        return dao.addScheduleState(state);
    }

    @Path("/assignments")
    @GET
    public List<PolicyAssignment> listAssignmenets() {
        return dao.listAssignments();
    }

    @Path("/assignments")
    @POST
    public OpResult addAssignmenet(PolicyAssignment pa) {
        return dao.addAssignment(pa);
    }

    @Path("/topologies")
    @GET
    public List<Topology> listTopologies() {
        return dao.listTopologies();
    }

    @Path("/topologies")
    @POST
    public OpResult addTopology(Topology t) {
        return dao.addTopology(t);
    }

    @Path("/topologies/batch")
    @POST
    public List<OpResult> addTopologies(List<Topology> topologies) {
        List<OpResult> results = new LinkedList<>();
        for (Topology t : topologies) {
            results.add(dao.addTopology(t));
        }
        return results;
    }

    @Path("/topologies/{topologyName}")
    @DELETE
    public OpResult removeTopology(@PathParam("topologyName") String topologyName) {
        return dao.removeTopology(topologyName);
    }

    @Path("/topologies")
    @DELETE
    public List<OpResult> removeTopologies(List<String> topologies) {
        List<OpResult> results = new LinkedList<>();
        for (String t : topologies) {
            results.add(dao.removeTopology(t));
        }
        return results;
    }

}
