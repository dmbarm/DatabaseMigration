package org.migrationtool.utils;

import org.migrationtool.exceptions.ChecksumGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ChecksumGenerator.class);

    private ChecksumGenerator() {}

    public static String generateWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            String generatedChecksum = bytesToHex(hash);
            logger.debug("Generated checksum: {}", generatedChecksum);
            return generatedChecksum;
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksumGenerationException("Failed to generate checksum with SHA-256", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}
