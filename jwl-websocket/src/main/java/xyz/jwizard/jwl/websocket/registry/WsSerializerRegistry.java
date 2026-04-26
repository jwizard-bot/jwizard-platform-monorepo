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
package xyz.jwizard.jwl.websocket.registry;

import xyz.jwizard.jwl.codec.envelope.EnvelopeSerializer;
import xyz.jwizard.jwl.codec.envelope.json.JsonBinaryEnvelopeSerializer;
import xyz.jwizard.jwl.codec.envelope.json.JsonTextEnvelopeSerializer;
import xyz.jwizard.jwl.codec.serialization.SerializerRegistry;
import xyz.jwizard.jwl.codec.serialization.json.JsonSerializer;

public class WsSerializerRegistry extends SerializerRegistry<EnvelopeSerializer<?>> {
    private WsSerializerRegistry() {
        super();
    }

    public static WsSerializerRegistry createWs() {
        return new WsSerializerRegistry();
    }

    public WsSerializerRegistry registerJsonDefaults(JsonSerializer jsonSerializer) {
        register(JsonTextEnvelopeSerializer.createDefault(jsonSerializer));
        register(JsonBinaryEnvelopeSerializer.createDefault(jsonSerializer));
        return this;
    }

    @Override
    public WsSerializerRegistry register(EnvelopeSerializer<?> serializer) {
        super.register(serializer);
        return this;
    }
}
