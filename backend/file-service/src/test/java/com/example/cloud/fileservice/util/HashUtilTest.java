package com.example.cloud.fileservice.util;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashUtilTest {

    @Test
    void sha256_shouldReturnCorrectHash() throws Exception {
        String input = "hello";
        ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes());

        String hash = HashUtil.sha256(stream);

        assertEquals(
                "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                hash
        );
    }
}