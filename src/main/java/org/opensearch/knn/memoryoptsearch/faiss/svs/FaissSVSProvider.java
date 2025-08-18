/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.knn.memoryoptsearch.faiss;

/**
 * Provider for returning an instance of {@link FaissSVS}.
 * <p>
 * This provider should consistently return the same instance across multiple calls.
 * It does not imply a singleton instance, but each provider instance should consistently return the same {@link FaissSVS} instance across
 * multiple calls.
 */
public interface FaissSVSProvider {
    /**
     * Return internal {@link FaissSVS}.
     *
     * @return {@link FaissSVS} instance it internally keeps.
     */
    FaissSVS getFaissSVS();
}
