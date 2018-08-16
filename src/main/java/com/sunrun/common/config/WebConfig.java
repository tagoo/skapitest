package com.sunrun.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.*;
import java.util.Date;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        SimpleModule timeModule = new JavaTimeModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        timeModule.addSerializer(LocalDateTime.class,LocalDateTimeSerializer.INSTANCE);
        timeModule.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        timeModule.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
        timeModule.addSerializer(Date.class, DateSerializer.instance);
        timeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        timeModule.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        timeModule.addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(timeModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(jackson2HttpMessageConverter);
    }
}
