package com.manusrao.nano.converter;   // adjust to your package

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Converter
public class InetAddressConverter implements AttributeConverter<InetAddress, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(InetAddress attribute) {
        return attribute == null ? null : attribute.getAddress();
    }

    @Override
    public InetAddress convertToEntityAttribute(byte[] dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return InetAddress.getByAddress(dbData);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address bytes (length " + dbData.length + ")", e);
        }
    }
}
