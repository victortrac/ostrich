/*
 * Copyright 2013 Bazaarvoice, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bazaarvoice.ostrich;

import java.util.Map;

/**
 * A map of key/value pairs that a service pool can use to choose among available back-end service end points.
 */
public interface PartitionContext {
    /**
     * Gets the default piece of context. In many cases, there is only a single piece of relevant context, which this
     * method should provide.
     *
     * @return The default context data.
     */
    Object get();

    /**
     * Gets the context for the specified key.
     * @param key The key for the desired context data.
     * @return The context data.
     */
    Object get(String key);

    /**
     * Gets a {@code Map} version of the context. The Map should be immutable.
     * @return A {@code Map} with the same key/value pairs as this context.
     */
    Map<String, Object> asMap();
}
