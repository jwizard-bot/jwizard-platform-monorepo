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
package xyz.jwizard.jwl.codec.serialization;

import xyz.jwizard.jwl.common.registry.GenericConcurrentRegistry;

public class SerializerRegistry<S extends Serializer> extends GenericConcurrentRegistry<String, S> {
    protected SerializerRegistry() {
        super();
    }

    public static SerializerRegistry<MessageSerializer> createDefault() {
        return new SerializerRegistry<>();
    }

    public static <S extends Serializer> SerializerRegistry<S> create() {
        return new SerializerRegistry<>();
    }

    public SerializerRegistry<S> register(S serializer) {
        super.register(serializer.getFormat().getFormatName(), serializer);
        return this;
    }

    public S get(SerializerFormat format) {
        return super.get(format.getFormatName());
    }
}
