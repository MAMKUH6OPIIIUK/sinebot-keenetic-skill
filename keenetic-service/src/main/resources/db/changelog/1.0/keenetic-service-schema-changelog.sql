--liquibase formatted sql

--
--changeset k.oshoev:1
CREATE TABLE IF NOT EXISTS vendors
(
    vendor_id bigserial NOT NULL PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE
);
COMMENT ON TABLE vendors
    IS 'Таблица поддерживаемых производителей роутеров';

CREATE TABLE IF NOT EXISTS properties
(
    property_id bigserial NOT NULL PRIMARY KEY,
    type varchar(50) NOT NULL,
    retrievable boolean NOT NULL
);
COMMENT ON TABLE properties
    IS 'Таблица управляемых свойств точек доступа';

CREATE TABLE IF NOT EXISTS access_points
(
    access_point_id bigserial NOT NULL PRIMARY KEY,
    type varchar(50) NOT NULL,
    band varchar(10) NOT NULL,
    interface_name varchar(50) NOT NULL
);
COMMENT ON TABLE access_points
    IS 'Таблица с информацией о точках доступа wi-fi';

CREATE TABLE IF NOT EXISTS access_points_properties
(
    access_point_id bigint NOT NULL,
    property_id bigint NOT NULL,
    CONSTRAINT access_points_properties_pkey PRIMARY KEY (access_point_id, property_id),
    CONSTRAINT fk_access_points_properties_point FOREIGN KEY (access_point_id)
            REFERENCES access_points (access_point_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
        CONSTRAINT fk_access_points_properties_property FOREIGN KEY (property_id)
            REFERENCES properties (property_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);
COMMENT ON TABLE access_points_properties
    IS 'Таблица связей между точками доступа и их управляемыми свойствами';

CREATE TABLE IF NOT EXISTS actions
(
    action_id bigserial NOT NULL PRIMARY KEY,
    type varchar(50) NOT NULL UNIQUE
);
COMMENT ON TABLE actions
    IS 'Таблица существующих типов действий над устройствами';

CREATE TABLE IF NOT EXISTS models
(
    model_id bigserial NOT NULL PRIMARY KEY,
    vendor_id bigint NOT NULL,
    name varchar(100) NOT NULL UNIQUE,
    CONSTRAINT fk_models_vendors FOREIGN KEY (vendor_id)
            REFERENCES vendors(vendor_id)
);
COMMENT ON TABLE models
    IS 'Таблица известных моделей роутеров';

CREATE TABLE IF NOT EXISTS models_access_points
(
    model_id bigint NOT NULL,
    access_point_id bigint NOT NULL,
    CONSTRAINT model_access_point_pkey PRIMARY KEY (model_id, access_point_id),
    CONSTRAINT fk_models_access_points_model FOREIGN KEY (model_id)
            REFERENCES models (model_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
        CONSTRAINT fk_models_access_points_point FOREIGN KEY (access_point_id)
            REFERENCES access_points (access_point_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);
COMMENT ON TABLE models_access_points
    IS 'Таблица связей между моделями роутеров и параметрами их точек доступа';

CREATE TABLE IF NOT EXISTS models_actions
(
    model_id bigint NOT NULL,
    action_id bigint NOT NULL,
    CONSTRAINT model_action_pkey PRIMARY KEY (model_id, action_id),
    CONSTRAINT fk_models_actions_model FOREIGN KEY (model_id)
            REFERENCES models (model_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT,
        CONSTRAINT fk_models_actions_action FOREIGN KEY (action_id)
            REFERENCES actions (action_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);
COMMENT ON TABLE models_actions
    IS 'Таблица связей между моделями роутеров и применимыми к ним управляющими действиями';

CREATE TABLE IF NOT EXISTS devices
(
    device_id bigserial NOT NULL PRIMARY KEY,
    user_id bigint NOT NULL,
    model_id bigint NOT NULL,
    name varchar(500) NOT NULL,
    description varchar(1000),
    domain_name varchar(100) NOT NULL UNIQUE,
    login varchar(50) NOT NULL,
    password varchar(500) NOT NULL,
    CONSTRAINT fk_devices_models FOREIGN KEY (model_id)
                REFERENCES models(model_id)
);
COMMENT ON TABLE devices
    IS 'Таблица с информацией о параметрах роутеров пользователей';