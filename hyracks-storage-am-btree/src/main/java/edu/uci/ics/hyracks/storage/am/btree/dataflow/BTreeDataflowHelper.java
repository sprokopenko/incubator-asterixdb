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

package edu.uci.ics.hyracks.storage.am.btree.dataflow;

import edu.uci.ics.hyracks.api.context.IHyracksTaskContext;
import edu.uci.ics.hyracks.api.exceptions.HyracksDataException;
import edu.uci.ics.hyracks.storage.am.btree.exceptions.BTreeException;
import edu.uci.ics.hyracks.storage.am.btree.frames.BTreeLeafFrameType;
import edu.uci.ics.hyracks.storage.am.btree.util.BTreeUtils;
import edu.uci.ics.hyracks.storage.am.common.api.IIndexIdProvider;
import edu.uci.ics.hyracks.storage.am.common.api.IOperationCallbackProvider;
import edu.uci.ics.hyracks.storage.am.common.api.ITreeIndex;
import edu.uci.ics.hyracks.storage.am.common.dataflow.IIndexOperatorDescriptor;
import edu.uci.ics.hyracks.storage.am.common.dataflow.TreeIndexDataflowHelper;

public class BTreeDataflowHelper extends TreeIndexDataflowHelper {
    public BTreeDataflowHelper(IIndexOperatorDescriptor opDesc, IHyracksTaskContext ctx,
            IOperationCallbackProvider opCallbackProvider, IIndexIdProvider indexIdProvider, int partition,
            boolean createIfNotExists) {
        super(opDesc, ctx, opCallbackProvider, indexIdProvider, partition, createIfNotExists);
    }

    @Override
    public ITreeIndex createIndexInstance() throws HyracksDataException {
        try {
            return BTreeUtils.createBTree(opDesc.getStorageManager().getBufferCache(ctx),
                    opCallbackProvider.getOperationCallback(), treeOpDesc.getTreeIndexTypeTraits(),
                    treeOpDesc.getTreeIndexComparatorFactories(), BTreeLeafFrameType.REGULAR_NSM);
        } catch (BTreeException e) {
            throw new HyracksDataException(e);
        }
    }
}
