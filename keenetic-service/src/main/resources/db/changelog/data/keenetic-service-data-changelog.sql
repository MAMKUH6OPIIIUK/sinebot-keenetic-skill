--liquibase formatted sql

--
--changeset k.oshoev:1
insert into vendors(vendor_id, name) values (1, 'Keenetic');
alter sequence vendors_vendor_id_seq restart with 2;

insert into properties(property_id, type, retrievable) values (1, 'state', true);
insert into properties(property_id, type, retrievable) values (2, 'wps', true);
alter sequence properties_property_id_seq restart with 3;

insert into access_points(access_point_id, type, band, interface_name) values (1, 'home', '2.4', 'WifiMaster0/AccessPoint0');
insert into access_points(access_point_id, type, band, interface_name) values (2, 'home', '5', 'WifiMaster1/AccessPoint0');
insert into access_points(access_point_id, type, band, interface_name) values (3, 'guest', '2.4', 'WifiMaster0/AccessPoint1');
insert into access_points(access_point_id, type, band, interface_name) values (4, 'guest', '5', 'WifiMaster1/AccessPoint1');
alter sequence access_points_access_point_id_seq restart with 5;

insert into access_points_properties(access_point_id, property_id) values (1, 1);
insert into access_points_properties(access_point_id, property_id) values (1, 2);
insert into access_points_properties(access_point_id, property_id) values (2, 1);
insert into access_points_properties(access_point_id, property_id) values (2, 2);
insert into access_points_properties(access_point_id, property_id) values (3, 1);
insert into access_points_properties(access_point_id, property_id) values (4, 1);

insert into actions(action_id, type) values (1, 'reload');
insert into actions(action_id, type) values (2, 'save_config');
insert into actions(action_id, type) values (3, 'check_connection');
alter sequence actions_action_id_seq restart with 4;

insert into models(model_id, vendor_id, name) values (1, 1, 'Extra');
insert into models(model_id, vendor_id, name) values (2, 1, 'Start II');
alter sequence models_model_id_seq restart with 3;

insert into models_access_points(model_id, access_point_id) values (1, 1);
insert into models_access_points(model_id, access_point_id) values (1, 2);
insert into models_access_points(model_id, access_point_id) values (1, 3);
insert into models_access_points(model_id, access_point_id) values (1, 4);
insert into models_access_points(model_id, access_point_id) values (2, 1);
insert into models_access_points(model_id, access_point_id) values (2, 3);

insert into models_actions(model_id, action_id) values (1, 1);
insert into models_actions(model_id, action_id) values (1, 2);
insert into models_actions(model_id, action_id) values (1, 3);
insert into models_actions(model_id, action_id) values (2, 1);
insert into models_actions(model_id, action_id) values (2, 2);
insert into models_actions(model_id, action_id) values (2, 3);