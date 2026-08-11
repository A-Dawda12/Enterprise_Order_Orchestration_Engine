package com.orderengine.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**

 * Local/dev-friendly timestamps: serialize {@link Instant} as Asia/Kolkata offset
 * (e.g. {@code 2026-08-09T20:30:00+05:30}) instead of UTC {@code ...Z}.
 *
 * Registers a Jackson {@link Module} bean (picked up by Boot) — avoids deprecated
 * {@code Jackson2ObjectMapperBuilderCustomizer}.
 */
@AutoConfiguration
public class OrderCommonJacksonAutoConfiguration {

    public static final ZoneId ZONE_ID = ZoneId.of("Asia/Kolkata");

    @Bean
    Module indiaInstantModule() {
        SimpleModule module = new SimpleModule("IndiaInstantModule");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        module.addSerializer(Instant.class, new JsonSerializer<>() {
            @Override
            public void serialize(
                    Instant value,
                    JsonGenerator gen,
                    SerializerProvider serializers
            ) throws IOException {
                gen.writeString(
                        value.atZone(ZONE_ID).format(formatter)
                );
            }
        });

        module.addDeserializer(Instant.class, new JsonDeserializer<>() {
            @Override
            public Instant deserialize(
                    JsonParser p,
                    DeserializationContext ctxt
            ) throws IOException {
                String text = p.getText();

                try {
                    return Instant.parse(text);
                } catch (DateTimeParseException ignored) {
                    return OffsetDateTime.parse(text).toInstant();
                }
            }
        });

        return module;

    }
}
