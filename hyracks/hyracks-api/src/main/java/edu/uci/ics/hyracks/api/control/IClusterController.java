/*
 * Copyright 2009-2010 by The Regents of the University of California
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License from
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uci.ics.hyracks.api.control;

import java.rmi.Remote;
import java.util.List;
import java.util.UUID;

import edu.uci.ics.hyracks.api.job.profiling.om.JobProfile;
import edu.uci.ics.hyracks.api.job.profiling.om.StageletProfile;

public interface IClusterController extends Remote {
    public NodeParameters registerNode(INodeController nodeController) throws Exception;

    public void unregisterNode(INodeController nodeController) throws Exception;

    public void notifyStageletComplete(UUID jobId, UUID stageId, int attempt, String nodeId, StageletProfile statistics)
            throws Exception;

    public void notifyStageletFailure(UUID jobId, UUID stageId, int attempt, String nodeId) throws Exception;

    public void nodeHeartbeat(String id) throws Exception;

    public void reportProfile(String id, List<JobProfile> profiles) throws Exception;
}