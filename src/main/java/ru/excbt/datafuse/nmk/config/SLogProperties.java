package ru.excbt.datafuse.nmk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slog")
public class SLogProperties {

    private final Settings settings = new Settings();

    public Settings getSettings() {
        return settings;
    }

    public static class Settings {

        private String schema = "slog";

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }
    }

}
