/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.asterix.common.replication;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hyracks.api.application.IClusterLifecycleListener.ClusterEventType;

public class ReplicaEvent {

    Replica replica;
    ClusterEventType eventType;

    public ReplicaEvent(Replica replica, ClusterEventType eventType) {
        this.replica = replica;
        this.eventType = eventType;
    }

    public Replica getReplica() {
        return replica;
    }

    public void setReplica(Replica replica) {
        this.replica = replica;
    }

    public ClusterEventType getEventType() {
        return eventType;
    }

    public void setEventType(ClusterEventType eventType) {
        this.eventType = eventType;
    }

    public void serialize(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        replica.writeFields(dos);
        dos.writeInt(eventType.ordinal());
    }

    public static ReplicaEvent create(DataInput input) throws IOException {
        Replica replica = Replica.create(input);
        ClusterEventType eventType = ClusterEventType.values()[input.readInt()];
        ReplicaEvent event = new ReplicaEvent(replica, eventType);
        return event;
    }

}
