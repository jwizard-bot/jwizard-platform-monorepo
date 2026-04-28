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
package xyz.jwizard.jwl.netclient.rest.jetty.spec;

import java.util.Map;

import org.eclipse.jetty.client.FormRequestContent;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.util.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.MessageSerializer;
import xyz.jwizard.jwl.common.reflect.LoadedViaReflection;
import xyz.jwizard.jwl.netclient.rest.spec.GenericRequestSpec;
import xyz.jwizard.jwl.netclient.rest.spec.HeaderConsumer;

@LoadedViaReflection
public class JettyFormBodyStrategy implements JettyBodyStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(JettyFormBodyStrategy.class);

    @Override
    public boolean supports(GenericRequestSpec spec) {
        return spec.getFormParams() != null && !spec.getFormParams().isEmpty();
    }

    @Override
    public Request.Content buildContent(GenericRequestSpec spec, MessageSerializer serializer,
                                        HeaderConsumer headerConsumer) {
        LOG.trace("Building form-urlencoded body for request: {}", spec.getUriPath());
        if (spec.getBody() != null) {
            throw new IllegalStateException("Cannot use both form parameters and raw body in " +
                "the same request");
        }
        final Fields fields = new Fields();
        for (final Map.Entry<String, String> entry : spec.getFormParams().entrySet()) {
            fields.put(entry.getKey(), entry.getValue());
        }
        return new FormRequestContent(fields);
    }
}
