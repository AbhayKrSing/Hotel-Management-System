package com.airbnb.admin.model;

import com.airbnb.common.AuditableEntry;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Stores global platform configuration key-value pairs (e.g. commission rate).
 */
@Entity
@Table(name = "system_config")
public class SystemConfig extends AuditableEntry {

    private UUID id;
    private String configKey;
    private String configValue;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "C_Config_Id", nullable = false, columnDefinition = "VARCHAR(36)")
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @Column(name = "C_Config_Key", unique = true, nullable = false)
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    @Column(name = "C_Config_Value", nullable = false)
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    @Column(name = "C_Description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
