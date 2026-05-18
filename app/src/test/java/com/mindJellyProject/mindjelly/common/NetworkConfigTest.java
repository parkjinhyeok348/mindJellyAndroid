package com.mindJellyProject.mindjelly.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NetworkConfigTest {
    @Test
    public void assetUrlUsesApiBaseUrlForRelativePaths() {
        assertEquals("http://10.0.2.2:8080/images/jelly.png", NetworkConfig.assetUrl("/images/jelly.png"));
    }

    @Test
    public void assetUrlKeepsAbsoluteUrls() {
        assertEquals("https://cdn.example.com/jelly.png", NetworkConfig.assetUrl("https://cdn.example.com/jelly.png"));
    }

    @Test
    public void assetUrlKeepsNullAsNull() {
        assertNull(NetworkConfig.assetUrl(null));
    }
}
