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
package org.apache.eagle.alert.engine.publisher.dedup;

import com.typesafe.config.Config;

public class DedupEventsStoreFactory {

    public enum DedupEventsStoreType {
        Mongo, ElasticSearch
    }

    ;

    private static DedupEventsStore customizedStore;

    private static MongoDedupEventsStore accessor;

    public static void customizeStore(DedupEventsStore store) {
        customizedStore = store;
    }

    public static DedupEventsStore getStore(DedupEventsStoreType type, Config config, String publishName) {
        if (customizedStore != null) {
            return customizedStore;
        }
        switch (type) {
            case Mongo:
                if (accessor == null) {
                    accessor = new MongoDedupEventsStore(config, publishName);
                }
                break;
            case ElasticSearch:
            default:
                break;
        }
        if (accessor == null) {
            throw new RuntimeException(String.format("Dedup events store type %s is NOT supportted", type));
        }
        return accessor;
    }

}
