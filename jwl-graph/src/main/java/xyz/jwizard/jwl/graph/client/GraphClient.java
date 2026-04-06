/*
 * Copyright 2026 by JWizard
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
package xyz.jwizard.jwl.graph.client;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

// transaction read/write
public interface GraphClient extends Closeable {
    List<Map<String, Object>> read(String query, Map<String, Object> parameters);

    List<Map<String, Object>> write(String query, Map<String, Object> parameters);

    void execute(String query, Map<String, Object> parameters);

    @Override
    void close();
}
