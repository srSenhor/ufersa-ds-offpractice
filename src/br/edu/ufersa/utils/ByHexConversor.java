package br.edu.ufersa.utils;

public class ByHexConversor {
    public static String convert(byte[] bytes) {

        StringBuilder converted = new StringBuilder();

        for (byte b : bytes) { converted.append(String.format("%02x", b)); }

        return converted.toString();
    }
}
