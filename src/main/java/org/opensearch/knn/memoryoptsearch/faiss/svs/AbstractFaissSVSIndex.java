/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.knn.memoryoptsearch.faiss;

import lombok.Getter;

public abstract class AbstractFaissSVSIndex extends FaissIndex implements FaissSVSProvider {
    @Getter
    protected FaissSVS faissSVS;
    protected FaissIndex flatVectors;

    public AbstractFaissSVSIndex(final String indexType, final FaissSVS faissSVS) {
        super(indexType);
        this.faissSVS = faissSVS;
    }
}
