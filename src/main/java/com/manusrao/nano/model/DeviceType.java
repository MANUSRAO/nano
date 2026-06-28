package com.manusrao.nano.model;   // adjust to your package

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum DeviceType {

    DESKTOP("desktop"),
    MOBILE("mobile"),
    TABLET("tablet"),
    UNKNOWN("unknown");

    private final String dbValue;

    DeviceType(String dbValue) {
        this.dbValue = dbValue;
    }

    public static DeviceType fromDbValue(String value) {
        for (DeviceType type : values()) {
            if (type.dbValue.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown device_type value: " + value);
    }

    /**
     * Converts between the Java constant (DESKTOP) and the exact MySQL ENUM
     * string ('desktop'). EnumType.STRING would store the constant name
     * ('DESKTOP') and break against the lowercase ENUM definition.
     */
    @jakarta.persistence.Converter
    public static class Converter implements AttributeConverter<DeviceType, String> {

        @Override
        public String convertToDatabaseColumn(DeviceType attribute) {
            return attribute == null ? null : attribute.dbValue;
        }

        @Override
        public DeviceType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : DeviceType.fromDbValue(dbData);
        }
    }
}
