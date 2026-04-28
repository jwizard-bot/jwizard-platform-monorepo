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
package xyz.jwizard.jwl.netclient.rest;

import xyz.jwizard.jwl.net.http.HttpMethod;
import xyz.jwizard.jwl.netclient.rest.spec.RequestSpec;

public interface RestClient {
    RequestSpec get(String uri);

    RequestSpec post(String uri);

    RequestSpec put(String uri);

    RequestSpec patch(String uri);

    RequestSpec delete(String uri);

    RequestSpec request(HttpMethod method, String uri);
}
