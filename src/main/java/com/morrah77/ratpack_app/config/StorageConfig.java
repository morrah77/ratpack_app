package com.morrah77.ratpack_app.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class StorageConfig {
    public String uri;
    public StorageConfig() {}
}
