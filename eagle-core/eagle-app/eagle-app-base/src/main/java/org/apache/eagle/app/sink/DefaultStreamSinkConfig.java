/**
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
package org.apache.eagle.app.sink;

import org.apache.eagle.metadata.model.StreamSinkConfig;

public class DefaultStreamSinkConfig implements StreamSinkConfig {
    private final Class<?> streamPersistClass;
    private static final String NONE_STORAGE_TYPE = "NONE";

    public DefaultStreamSinkConfig(Class<?> streamPersistClass) {
        this.streamPersistClass = streamPersistClass;
    }

    @Override
    public String getType() {
        return NONE_STORAGE_TYPE;
    }

    public Class<?> getSinkType() {
        return streamPersistClass;
    }

    @Override
    public Class<? extends StreamSinkConfig> getConfigType() {
        return DefaultStreamSinkConfig.class;
    }
}