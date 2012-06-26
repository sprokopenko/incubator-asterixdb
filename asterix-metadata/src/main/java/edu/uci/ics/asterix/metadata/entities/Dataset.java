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

package edu.uci.ics.asterix.metadata.entities;

import edu.uci.ics.asterix.common.config.DatasetConfig.DatasetType;
import edu.uci.ics.asterix.metadata.IDatasetDetails;
import edu.uci.ics.asterix.metadata.MetadataCache;
import edu.uci.ics.asterix.metadata.api.IMetadataEntity;

/**
 * Metadata describing a dataset.
 */
public class Dataset implements IMetadataEntity {

    private static final long serialVersionUID = 1L;

    private final String dataverseName;
    // Enforced to be unique within a dataverse.
    private final String datasetName;
    // Type of values stored in this dataset.
    private final String datatypeName;
    private final DatasetType datasetType;
    private IDatasetDetails datasetDetails;

    public Dataset(String dataverseName, String datasetName, String datatypeName, IDatasetDetails datasetDetails,
            DatasetType datasetType) {
        this.dataverseName = dataverseName;
        this.datasetName = datasetName;
        this.datatypeName = datatypeName;
        this.datasetType = datasetType;
        this.datasetDetails = datasetDetails;
    }

    public String getDataverseName() {
        return dataverseName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public String getDatatypeName() {
        return datatypeName;
    }

    public DatasetType getType() {
        return datasetType;
    }

    public IDatasetDetails getDatasetDetails() {
        return datasetDetails;
    }

    public void setDatasetDetails(IDatasetDetails datasetDetails) {
        this.datasetDetails = datasetDetails;
    }

    @Override
    public Object addToCache(MetadataCache cache) {
        return cache.addDatasetIfNotExists(this);
    }

    @Override
    public Object dropFromCache(MetadataCache cache) {
        return cache.dropDataset(this);
    }
}