package com.project.rentoday.domain.reservation.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;
@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {
    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.toHours();
    }

    @Override
    public Duration convertToEntityAttribute(Long hours) {
        return hours == null ? null : Duration.ofHours(hours);
    }
}
