package com.evia.portal.userportal.core.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Upload upload = new Upload();

    @Getter
    @Setter
    public static class Upload {
        private String resourcesServerStore;
        private String resourcesServerStoreUrl;
    }



}
