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

import org.eclipse.jetty.client.BytesRequestContent;
import org.eclipse.jetty.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.jwizard.jwl.codec.serialization.MessageSerializer;
import xyz.jwizard.jwl.common.reflect.LoadedViaReflection;
import xyz.jwizard.jwl.net.http.header.CommonHttpHeaderName;
import xyz.jwizard.jwl.netclient.rest.spec.GenericRequestSpec;
import xyz.jwizard.jwl.netclient.rest.spec.HeaderConsumer;

@LoadedViaReflection
public class JettyRawBodyStrategy implements JettyBodyStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(JettyRawBodyStrategy.class);

    @Override
    public boolean supports(GenericRequestSpec spec) {
        return spec.getBody() != null &&
            (spec.getFormParams() == null || spec.getFormParams().isEmpty());
    }

    @Override
    public Request.Content buildContent(GenericRequestSpec spec, MessageSerializer serializer,
                                        HeaderConsumer headerConsumer) {
        final String mimeType = serializer.format().getMimeType();
        LOG.trace("Building raw body, format: {}, mime type: {}", serializer.format().getFormat(),
            mimeType);
        if (mimeType != null) {
            headerConsumer.addHeader(CommonHttpHeaderName.CONTENT_TYPE, mimeType);
        }
        final byte[] serializedBytes = serializer.serializeToBytes(spec.getBody());
        return new BytesRequestContent(serializedBytes);
    }
}
