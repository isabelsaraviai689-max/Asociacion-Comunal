package com.sv.grupo11.asociacioncomunal.controller;

import com.sv.grupo11.asociacioncomunal.config.DataSourceConfig;

/** Expone a la UI informacion tecnica del sistema (estado del pool de conexiones). */
public class SistemaController {

    private final DataSourceConfig dataSourceConfig;

    public SistemaController(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public String estadoPool() {
        return dataSourceConfig.resumenPool();
    }
}
