/*
PostgreSQL Backup
Database: xiyou/public
Backup Time: 2023-08-21 00:15:43
*/

DROP SEQUENCE IF EXISTS "public"."base_station_base_station_id_seq";
DROP SEQUENCE IF EXISTS "public"."beacon_beacon_id_seq";
DROP SEQUENCE IF EXISTS "public"."building_building_id_seq";
DROP SEQUENCE IF EXISTS "public"."config_config_id_seq";
DROP SEQUENCE IF EXISTS "public"."local_server_local_server_id_seq";
DROP SEQUENCE IF EXISTS "public"."location_object_location_object_id_seq";
DROP SEQUENCE IF EXISTS "public"."map_map_id_seq";
DROP SEQUENCE IF EXISTS "public"."xy_alarm_alarm_id_seq";
DROP SEQUENCE IF EXISTS "public"."xy_fence_fence_id_seq";
DROP SEQUENCE IF EXISTS "public"."xy_user_user_id_seq";
DROP TABLE IF EXISTS "public"."xy_alarm";
DROP TABLE IF EXISTS "public"."xy_base_station";
DROP TABLE IF EXISTS "public"."xy_beacon";
DROP TABLE IF EXISTS "public"."xy_bind_lom";
DROP TABLE IF EXISTS "public"."xy_building";
DROP TABLE IF EXISTS "public"."xy_config";
DROP TABLE IF EXISTS "public"."xy_fence";
DROP TABLE IF EXISTS "public"."xy_local_server";
DROP TABLE IF EXISTS "public"."xy_location_object";
DROP TABLE IF EXISTS "public"."xy_map";
DROP TABLE IF EXISTS "public"."xy_user";
CREATE SEQUENCE "base_station_base_station_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "beacon_beacon_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "building_building_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "config_config_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "local_server_local_server_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "location_object_location_object_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "map_map_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "xy_alarm_alarm_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "xy_fence_fence_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE SEQUENCE "xy_user_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
CREATE TABLE "xy_alarm" (
  "alarm_id" int4 NOT NULL DEFAULT nextval('xy_alarm_alarm_id_seq'::regclass),
  "alarm_location_object" int4 NOT NULL,
  "alarm_type" int2 NOT NULL,
  "alarm_status" bool NOT NULL,
  "alarm_time" int8 NOT NULL,
  "alarm_fence" int4,
  "alarm_map" int4 NOT NULL
)
;
ALTER TABLE "xy_alarm" OWNER TO "postgres";
COMMENT ON COLUMN "xy_alarm"."alarm_id" IS '报警数据库ID';
COMMENT ON COLUMN "xy_alarm"."alarm_location_object" IS '报警绑定的对象';
COMMENT ON COLUMN "xy_alarm"."alarm_type" IS '报警类型';
COMMENT ON COLUMN "xy_alarm"."alarm_status" IS '报警状态（已处理/未处理）';
COMMENT ON COLUMN "xy_alarm"."alarm_time" IS '报警时间戳';
COMMENT ON COLUMN "xy_alarm"."alarm_fence" IS '报警所属围栏，进地图报警为null';
COMMENT ON COLUMN "xy_alarm"."alarm_map" IS '报警所属地图';
CREATE TABLE "xy_base_station" (
  "base_station_id" int4 NOT NULL DEFAULT nextval('base_station_base_station_id_seq'::regclass),
  "base_station_mac" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_ip" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_elevation" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_azimuth" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_rotation" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_error_degree" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_x" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_y" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_z" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_online" bool NOT NULL,
  "base_station_last_time" int8 NOT NULL,
  "base_station_map" int4 NOT NULL,
  "base_station_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_product" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "base_station_channels" jsonb NOT NULL
)
;
ALTER TABLE "xy_base_station" OWNER TO "postgres";
COMMENT ON COLUMN "xy_base_station"."base_station_id" IS '基站数据库ID';
COMMENT ON COLUMN "xy_base_station"."base_station_mac" IS '基站mac地址';
COMMENT ON COLUMN "xy_base_station"."base_station_ip" IS '基站局域网ip地址';
COMMENT ON COLUMN "xy_base_station"."base_station_elevation" IS '基站仰角';
COMMENT ON COLUMN "xy_base_station"."base_station_azimuth" IS '基站方位角';
COMMENT ON COLUMN "xy_base_station"."base_station_rotation" IS '基站旋转角';
COMMENT ON COLUMN "xy_base_station"."base_station_error_degree" IS '基站误差角度';
COMMENT ON COLUMN "xy_base_station"."base_station_x" IS '基站x坐标';
COMMENT ON COLUMN "xy_base_station"."base_station_y" IS '基站y坐标';
COMMENT ON COLUMN "xy_base_station"."base_station_z" IS '基站安装高度';
COMMENT ON COLUMN "xy_base_station"."base_station_online" IS '基站是否在线';
COMMENT ON COLUMN "xy_base_station"."base_station_last_time" IS '基站最后通讯13位时间戳';
COMMENT ON COLUMN "xy_base_station"."base_station_map" IS '基站所属地图';
COMMENT ON COLUMN "xy_base_station"."base_station_name" IS '基站名称';
COMMENT ON COLUMN "xy_base_station"."base_station_type" IS '基站类型（AOA/RSSI）';
COMMENT ON COLUMN "xy_base_station"."base_station_product" IS '基站产品型号';
COMMENT ON COLUMN "xy_base_station"."base_station_channels" IS '基站信道列表';
CREATE TABLE "xy_beacon" (
  "beacon_id" int4 NOT NULL DEFAULT nextval('beacon_beacon_id_seq'::regclass),
  "beacon_mac" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_x" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_y" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_z" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_online" bool NOT NULL,
  "beacon_last_time" int8 NOT NULL,
  "beacon_join_time" int8 NOT NULL,
  "beacon_product" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_type" int2 NOT NULL,
  "beacon_allow" bool NOT NULL,
  "beacon_location_object" int4,
  "beacon_remark" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_rssi" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_channel" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_frequency" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_power_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_sos" bool NOT NULL,
  "beacon_sos_time" int8 NOT NULL,
  "beacon_battery" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_last_gateway" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_nearest_gateway" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_map_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_zone_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_zone_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_last_local_server_mac" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "beacon_last_fence_ids" int4[] NOT NULL
)
;
ALTER TABLE "xy_beacon" OWNER TO "postgres";
COMMENT ON COLUMN "xy_beacon"."beacon_id" IS '信标数据库ID';
COMMENT ON COLUMN "xy_beacon"."beacon_mac" IS '信标mac地址';
COMMENT ON COLUMN "xy_beacon"."beacon_x" IS '信标x坐标';
COMMENT ON COLUMN "xy_beacon"."beacon_y" IS '信标y坐标';
COMMENT ON COLUMN "xy_beacon"."beacon_z" IS '信标安装高度';
COMMENT ON COLUMN "xy_beacon"."beacon_online" IS '信标是否在线';
COMMENT ON COLUMN "xy_beacon"."beacon_last_time" IS '信标最后通讯时间';
COMMENT ON COLUMN "xy_beacon"."beacon_join_time" IS '信标加入时间';
COMMENT ON COLUMN "xy_beacon"."beacon_product" IS '信标产品型号';
COMMENT ON COLUMN "xy_beacon"."beacon_type" IS '信标类型（1任务；2设备；3物品；4车辆）';
COMMENT ON COLUMN "xy_beacon"."beacon_allow" IS '信标是否录入';
COMMENT ON COLUMN "xy_beacon"."beacon_location_object" IS '信标绑定对象';
COMMENT ON COLUMN "xy_beacon"."beacon_remark" IS '信标备注';
COMMENT ON COLUMN "xy_beacon"."beacon_rssi" IS '信标RSSI值';
COMMENT ON COLUMN "xy_beacon"."beacon_channel" IS '信标信道';
COMMENT ON COLUMN "xy_beacon"."beacon_frequency" IS '信标频率';
COMMENT ON COLUMN "xy_beacon"."beacon_power_type" IS '''TI'' | ''Nordic''';
COMMENT ON COLUMN "xy_beacon"."beacon_sos" IS '信标报警开关';
COMMENT ON COLUMN "xy_beacon"."beacon_sos_time" IS '信标按下报警时间';
COMMENT ON COLUMN "xy_beacon"."beacon_battery" IS '信标电量百分比';
COMMENT ON COLUMN "xy_beacon"."beacon_last_gateway" IS '信标最后一次发包的基站mac';
COMMENT ON COLUMN "xy_beacon"."beacon_nearest_gateway" IS '信标最近的基站mac';
COMMENT ON COLUMN "xy_beacon"."beacon_map_id" IS '信标所处地图图层ID';
COMMENT ON COLUMN "xy_beacon"."beacon_zone_id" IS '信标所处区域ID';
COMMENT ON COLUMN "xy_beacon"."beacon_zone_name" IS '信标所处区域名称';
COMMENT ON COLUMN "xy_beacon"."beacon_last_local_server_mac" IS '信标最后一次发包的本地服务mac';
COMMENT ON COLUMN "xy_beacon"."beacon_last_fence_ids" IS '信标最后一次所在的';
CREATE TABLE "xy_bind_lom" (
  "bind_lom_location_object" int4 NOT NULL,
  "bind_lom_map" int4 NOT NULL
)
;
ALTER TABLE "xy_bind_lom" OWNER TO "postgres";
COMMENT ON COLUMN "xy_bind_lom"."bind_lom_location_object" IS '定位对象绑定地图-定位对象';
COMMENT ON COLUMN "xy_bind_lom"."bind_lom_map" IS '定位对象绑定地图-地图';
CREATE TABLE "xy_building" (
  "building_id" int4 NOT NULL DEFAULT nextval('building_building_id_seq'::regclass),
  "building_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "building_address" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "building_img" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "building_img_update_time" int8 NOT NULL
)
;
ALTER TABLE "xy_building" OWNER TO "postgres";
COMMENT ON COLUMN "xy_building"."building_id" IS '建筑数据库ID';
COMMENT ON COLUMN "xy_building"."building_name" IS '建筑名称';
COMMENT ON COLUMN "xy_building"."building_address" IS '建筑地址';
COMMENT ON COLUMN "xy_building"."building_img" IS '建筑示意图文件名';
COMMENT ON COLUMN "xy_building"."building_img_update_time" IS '建筑示意图最近上传时间';
CREATE TABLE "xy_config" (
  "config_id" int4 NOT NULL DEFAULT nextval('config_config_id_seq'::regclass),
  "config_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "config_name" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "config_value" int2 NOT NULL,
  "config_extra" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "config_order" int2 NOT NULL,
  "config_desc" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "xy_config" OWNER TO "postgres";
COMMENT ON COLUMN "xy_config"."config_id" IS '配置项数据库ID';
COMMENT ON COLUMN "xy_config"."config_type" IS '配置项类型分类';
COMMENT ON COLUMN "xy_config"."config_name" IS '配置项显示名称';
COMMENT ON COLUMN "xy_config"."config_value" IS '配置项值';
COMMENT ON COLUMN "xy_config"."config_extra" IS '配置项附加值';
COMMENT ON COLUMN "xy_config"."config_order" IS '配置项排序';
COMMENT ON COLUMN "xy_config"."config_desc" IS '配置项描述';
CREATE TABLE "xy_fence" (
  "fence_id" int4 NOT NULL DEFAULT nextval('xy_fence_fence_id_seq'::regclass),
  "fence_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "fence_map" int4 NOT NULL,
  "fence_type" int2 NOT NULL,
  "fence_status" bool NOT NULL,
  "fence_content" geometry NOT NULL
)
;
ALTER TABLE "xy_fence" OWNER TO "postgres";
COMMENT ON COLUMN "xy_fence"."fence_id" IS '围栏数据库ID';
COMMENT ON COLUMN "xy_fence"."fence_name" IS '围栏名称';
COMMENT ON COLUMN "xy_fence"."fence_map" IS '围栏所属地图';
COMMENT ON COLUMN "xy_fence"."fence_type" IS '围栏类型（进入/离开）';
COMMENT ON COLUMN "xy_fence"."fence_status" IS '围栏状态（启用/禁用）';
COMMENT ON COLUMN "xy_fence"."fence_content" IS '围栏多边形值';
CREATE TABLE "xy_local_server" (
  "local_server_id" int4 NOT NULL DEFAULT nextval('local_server_local_server_id_seq'::regclass),
  "local_server_mac" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_online" bool NOT NULL,
  "local_server_cle_startup" bool NOT NULL,
  "local_server_cle_activation" bool NOT NULL,
  "local_server_cle_code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cle_license" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_remark" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_building" int4,
  "local_server_cpa_id" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cpa_project" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cpa_category" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cpa_version" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cpa_file" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_last_time" int8 NOT NULL,
  "local_server_cle_license_expire_time" int8 NOT NULL,
  "local_server_ip" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cle_version" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "local_server_cle_license_update_time" int8 NOT NULL,
  "local_server_cpa_file_update_time" int8 NOT NULL
)
;
ALTER TABLE "xy_local_server" OWNER TO "postgres";
COMMENT ON COLUMN "xy_local_server"."local_server_id" IS '本地服务数据库ID';
COMMENT ON COLUMN "xy_local_server"."local_server_mac" IS '本地服务所在机器mac地址';
COMMENT ON COLUMN "xy_local_server"."local_server_online" IS '本地服务是否在线';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_startup" IS '本地服务核芯物联CLE服务是否启动状态';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_activation" IS '本地服务核芯物联CLE服务是否激活状态';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_code" IS '本地服务核芯物联CLE服务机器码';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_license" IS '本地服务核芯物联CLE服务授权文件地址';
COMMENT ON COLUMN "xy_local_server"."local_server_remark" IS '本地服务备注';
COMMENT ON COLUMN "xy_local_server"."local_server_building" IS '本地服务绑定的建筑';
COMMENT ON COLUMN "xy_local_server"."local_server_cpa_id" IS '本地服务核芯物联CLE所传CPA文件中项目ID';
COMMENT ON COLUMN "xy_local_server"."local_server_cpa_project" IS '本地服务核芯物联CLE所传CPA文件中项目名称';
COMMENT ON COLUMN "xy_local_server"."local_server_cpa_category" IS '本地服务核芯物联CLE所传CPA文件中的项目分类';
COMMENT ON COLUMN "xy_local_server"."local_server_cpa_version" IS '本地服务核芯物联CLE所传CPA版本';
COMMENT ON COLUMN "xy_local_server"."local_server_cpa_file" IS '本地服务核芯物联CLE所传CPA文件名';
COMMENT ON COLUMN "xy_local_server"."local_server_last_time" IS '本地服务最后通讯13位时间戳';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_license_expire_time" IS '本地服务授权过期时间';
COMMENT ON COLUMN "xy_local_server"."local_server_ip" IS '本地服务所在机器局域网ip地址';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_version" IS '本地服务核芯物联CLE服务软件版本';
COMMENT ON COLUMN "xy_local_server"."local_server_cle_license_update_time" IS '本地服务授权最近上传时间';
COMMENT ON COLUMN "xy_local_server"."local_server_cpa_file_update_time" IS '本地服务核芯物联CLE所传CPA文件最近上传时间';
CREATE TABLE "xy_location_object" (
  "location_object_id" int4 NOT NULL DEFAULT nextval('location_object_location_object_id_seq'::regclass),
  "location_object_name" varchar(255) COLLATE "pg_catalog"."ja-JP-x-icu" NOT NULL,
  "location_object_type" int2 NOT NULL,
  "location_object_img" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "location_object_beacon" int4,
  "location_object_img_update_time" int8 NOT NULL,
  "location_object_create_time" int8 NOT NULL
)
;
ALTER TABLE "xy_location_object" OWNER TO "postgres";
COMMENT ON COLUMN "xy_location_object"."location_object_id" IS '定位对象数据库ID';
COMMENT ON COLUMN "xy_location_object"."location_object_name" IS '定位对象名称';
COMMENT ON COLUMN "xy_location_object"."location_object_type" IS '定位对象所属类型（1任务；2设备；3物品；4车辆）';
COMMENT ON COLUMN "xy_location_object"."location_object_img" IS '定位对象图标';
COMMENT ON COLUMN "xy_location_object"."location_object_beacon" IS '定位对象所绑定的信标';
COMMENT ON COLUMN "xy_location_object"."location_object_img_update_time" IS '定位对象图标最近上传时间';
COMMENT ON COLUMN "xy_location_object"."location_object_create_time" IS '定位对象创建时间';
CREATE TABLE "xy_map" (
  "map_id" int4 NOT NULL DEFAULT nextval('map_map_id_seq'::regclass),
  "map_cpa_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "map_cpa_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_model_type" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
  "map_width" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_height" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_width_pixel" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_height_pixel" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_meters_per_pixel" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_origin_pixel_x" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_origin_pixel_y" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "map_local_server" int4 NOT NULL,
  "map_cpa_areas" jsonb NOT NULL,
  "map_update_time" int8 NOT NULL
)
;
ALTER TABLE "xy_map" OWNER TO "postgres";
COMMENT ON COLUMN "xy_map"."map_id" IS '地图数据库ID';
COMMENT ON COLUMN "xy_map"."map_cpa_id" IS '地图在CPA文件中的ID';
COMMENT ON COLUMN "xy_map"."map_cpa_name" IS '地图在CPA文件中的名称';
COMMENT ON COLUMN "xy_map"."map_model_type" IS '地图在CPA文件中的类型（''image'' | ''collada'' | ''zip''）';
COMMENT ON COLUMN "xy_map"."map_width" IS '地图在CPA文件中定义的宽度';
COMMENT ON COLUMN "xy_map"."map_height" IS '地图在CPA文件中定义的高度';
COMMENT ON COLUMN "xy_map"."map_width_pixel" IS '地图在CPA文件中定义的像素宽度';
COMMENT ON COLUMN "xy_map"."map_height_pixel" IS '地图在CPA文件中定义的像素高度';
COMMENT ON COLUMN "xy_map"."map_meters_per_pixel" IS '地图在CPA文件中定义的米每px';
COMMENT ON COLUMN "xy_map"."map_origin_pixel_x" IS '地图在CPA文件中相对坐标原点偏移x';
COMMENT ON COLUMN "xy_map"."map_origin_pixel_y" IS '地图在CPA文件中相对坐标原点偏移y';
COMMENT ON COLUMN "xy_map"."map_local_server" IS '地图所属本地服务';
COMMENT ON COLUMN "xy_map"."map_cpa_areas" IS '地图在CPA文件中定义的区域信息';
COMMENT ON COLUMN "xy_map"."map_update_time" IS '地图在CPA文件中最近上传时间';
CREATE TABLE "xy_user" (
  "user_id" int4 NOT NULL DEFAULT nextval('xy_user_user_id_seq'::regclass),
  "user_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "user_pass" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "user_email" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "user_phone" varchar(50) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "xy_user" OWNER TO "postgres";
COMMENT ON COLUMN "xy_user"."user_id" IS '用户数据库ID';
COMMENT ON COLUMN "xy_user"."user_name" IS '用户名';
COMMENT ON COLUMN "xy_user"."user_pass" IS '用户密码';
COMMENT ON COLUMN "xy_user"."user_email" IS '用户邮箱';
COMMENT ON COLUMN "xy_user"."user_phone" IS '用户手机号';
BEGIN;
LOCK TABLE "public"."xy_alarm" IN SHARE MODE;
DELETE FROM "public"."xy_alarm";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_base_station" IN SHARE MODE;
DELETE FROM "public"."xy_base_station";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_beacon" IN SHARE MODE;
DELETE FROM "public"."xy_beacon";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_bind_lom" IN SHARE MODE;
DELETE FROM "public"."xy_bind_lom";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_building" IN SHARE MODE;
DELETE FROM "public"."xy_building";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_config" IN SHARE MODE;
DELETE FROM "public"."xy_config";
INSERT INTO "public"."xy_config" ("config_id","config_type","config_name","config_value","config_extra","config_order","config_desc") VALUES (1, 'DB_VERSION', '数据库版本', 1, 'V1.0.0', 1, '数据库版本'),(2, 'ALARM_SEND_MAIL_SWITCH', '报警是否发送邮件', 0, '', 1, '报警是否发送邮件，0不发送，1发送')
;
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_fence" IN SHARE MODE;
DELETE FROM "public"."xy_fence";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_local_server" IN SHARE MODE;
DELETE FROM "public"."xy_local_server";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_location_object" IN SHARE MODE;
DELETE FROM "public"."xy_location_object";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_map" IN SHARE MODE;
DELETE FROM "public"."xy_map";
COMMIT;
BEGIN;
LOCK TABLE "public"."xy_user" IN SHARE MODE;
DELETE FROM "public"."xy_user";
INSERT INTO "public"."xy_user" ("user_id","user_name","user_pass","user_email","user_phone") VALUES (1, 'admin', '{bcrypt}$2a$10$sF8kB4tqJzLEvpRcxM0H9utri/.P0NABz3AWjS...rxZqqeF5EsqW', 'liyi@ichiken-tky.com', '')
;
COMMIT;
ALTER TABLE "xy_alarm" ADD CONSTRAINT "xy_alarm_pkey" PRIMARY KEY ("alarm_id");
CREATE INDEX "xy_alarm_alarm_id_idx" ON "xy_alarm" USING hash (
  "alarm_id" "pg_catalog"."int4_ops"
);
CREATE INDEX "xy_alarm_alarm_location_object_idx" ON "xy_alarm" USING btree (
  "alarm_location_object" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "xy_alarm_alarm_time_idx" ON "xy_alarm" USING btree (
  "alarm_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "xy_alarm_alarm_type_idx" ON "xy_alarm" USING btree (
  "alarm_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
ALTER TABLE "xy_base_station" ADD CONSTRAINT "base_station_pkey" PRIMARY KEY ("base_station_id");
CREATE INDEX "base_station_base_station_id_idx" ON "xy_base_station" USING hash (
  "base_station_id" "pg_catalog"."int4_ops"
);
CREATE INDEX "base_station_base_station_ip_idx" ON "xy_base_station" USING btree (
  "base_station_ip" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "base_station_base_station_mac_idx" ON "xy_base_station" USING btree (
  "base_station_mac" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "base_station_base_station_map_idx" ON "xy_base_station" USING btree (
  "base_station_map" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "xy_base_station_base_station_last_time_idx" ON "xy_base_station" USING btree (
  "base_station_last_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
ALTER TABLE "xy_beacon" ADD CONSTRAINT "beacon_pkey" PRIMARY KEY ("beacon_id");
CREATE INDEX "beacon_beacon_allow_idx" ON "xy_beacon" USING btree (
  "beacon_allow" "pg_catalog"."bool_ops" ASC NULLS LAST
);
CREATE INDEX "beacon_beacon_id_idx" ON "xy_beacon" USING hash (
  "beacon_id" "pg_catalog"."int4_ops"
);
CREATE INDEX "beacon_beacon_last_time_idx" ON "xy_beacon" USING btree (
  "beacon_last_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "beacon_beacon_location_object_idx" ON "xy_beacon" USING btree (
  "beacon_location_object" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE UNIQUE INDEX "beacon_beacon_mac_idx" ON "xy_beacon" USING btree (
  "beacon_mac" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "beacon_beacon_map_id_idx" ON "xy_beacon" USING btree (
  "beacon_map_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "beacon_beacon_type_idx" ON "xy_beacon" USING btree (
  "beacon_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "xy_beacon_beacon_product_idx" ON "xy_beacon" USING btree (
  "beacon_product" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "bind_lom_bind_lom_location_object_idx" ON "xy_bind_lom" USING btree (
  "bind_lom_location_object" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "bind_lom_bind_lom_map_idx" ON "xy_bind_lom" USING btree (
  "bind_lom_map" "pg_catalog"."int4_ops" ASC NULLS LAST
);
ALTER TABLE "xy_building" ADD CONSTRAINT "building_pkey" PRIMARY KEY ("building_id");
CREATE INDEX "building_building_id_idx" ON "xy_building" USING hash (
  "building_id" "pg_catalog"."int4_ops"
);
CREATE UNIQUE INDEX "building_building_name_idx" ON "xy_building" USING btree (
  "building_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
ALTER TABLE "xy_config" ADD CONSTRAINT "config_pkey" PRIMARY KEY ("config_id");
CREATE INDEX "config_config_id_idx" ON "xy_config" USING hash (
  "config_id" "pg_catalog"."int4_ops"
);
CREATE INDEX "config_config_order_idx" ON "xy_config" USING btree (
  "config_order" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "config_config_type_idx" ON "xy_config" USING btree (
  "config_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "config_config_value_idx" ON "xy_config" USING btree (
  "config_value" "pg_catalog"."int2_ops" ASC NULLS LAST
);
ALTER TABLE "xy_fence" ADD CONSTRAINT "xy_fence_pkey" PRIMARY KEY ("fence_id");
CREATE INDEX "xy_fence_fence_content_idx" ON "xy_fence" USING gist (
  "fence_content" "public"."gist_geometry_ops_2d"
);
CREATE INDEX "xy_fence_fence_id_idx" ON "xy_fence" USING hash (
  "fence_id" "pg_catalog"."int4_ops"
);
CREATE INDEX "xy_fence_fence_map_idx" ON "xy_fence" USING btree (
  "fence_map" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "xy_fence_fence_name_idx" ON "xy_fence" USING btree (
  "fence_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "xy_fence_fence_type_idx" ON "xy_fence" USING btree (
  "fence_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
ALTER TABLE "xy_local_server" ADD CONSTRAINT "local_server_pkey" PRIMARY KEY ("local_server_id");
CREATE INDEX "local_server_local_server_building_idx" ON "xy_local_server" USING btree (
  "local_server_building" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "local_server_local_server_cpa_file_idx" ON "xy_local_server" USING btree (
  "local_server_cpa_file" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "local_server_local_server_cpa_project_idx" ON "xy_local_server" USING btree (
  "local_server_cpa_project" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "local_server_local_server_id_idx" ON "xy_local_server" USING hash (
  "local_server_id" "pg_catalog"."int4_ops"
);
CREATE UNIQUE INDEX "local_server_local_server_mac_idx" ON "xy_local_server" USING btree (
  "local_server_mac" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "xy_local_server_local_server_last_time_idx" ON "xy_local_server" USING btree (
  "local_server_last_time" "pg_catalog"."int8_ops" ASC NULLS LAST
);
ALTER TABLE "xy_location_object" ADD CONSTRAINT "location_object_pkey" PRIMARY KEY ("location_object_id");
CREATE INDEX "location_object_location_object_beacon_idx" ON "xy_location_object" USING btree (
  "location_object_beacon" "pg_catalog"."int4_ops" ASC NULLS LAST
);
CREATE INDEX "location_object_location_object_id_idx" ON "xy_location_object" USING hash (
  "location_object_id" "pg_catalog"."int4_ops"
);
CREATE UNIQUE INDEX "location_object_location_object_name_idx" ON "xy_location_object" USING btree (
  "location_object_name" COLLATE "pg_catalog"."ja-JP-x-icu" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "xy_location_object_location_object_type_idx" ON "xy_location_object" USING btree (
  "location_object_type" "pg_catalog"."int2_ops" ASC NULLS LAST
);
ALTER TABLE "xy_map" ADD CONSTRAINT "map_pkey" PRIMARY KEY ("map_id");
CREATE INDEX "map_map_cpa_id_idx" ON "xy_map" USING btree (
  "map_cpa_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "map_map_cpa_name_idx" ON "xy_map" USING btree (
  "map_cpa_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "map_map_id_idx" ON "xy_map" USING hash (
  "map_id" "pg_catalog"."int4_ops"
);
CREATE INDEX "map_map_local_server_idx" ON "xy_map" USING btree (
  "map_local_server" "pg_catalog"."int4_ops" ASC NULLS LAST
);
ALTER TABLE "xy_user" ADD CONSTRAINT "xy_user_pkey" PRIMARY KEY ("user_id");
CREATE INDEX "xy_user_user_id_idx" ON "xy_user" USING hash (
  "user_id" "pg_catalog"."int4_ops"
);
CREATE UNIQUE INDEX "xy_user_user_name_idx" ON "xy_user" USING btree (
  "user_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
ALTER TABLE "xy_alarm" ADD CONSTRAINT "xy_alarm_alarm_fence_fkey" FOREIGN KEY ("alarm_fence") REFERENCES "public"."xy_fence" ("fence_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_alarm" ADD CONSTRAINT "xy_alarm_alarm_location_object_fkey" FOREIGN KEY ("alarm_location_object") REFERENCES "public"."xy_location_object" ("location_object_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_alarm" ADD CONSTRAINT "xy_alarm_alarm_map_fkey" FOREIGN KEY ("alarm_map") REFERENCES "public"."xy_map" ("map_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_base_station" ADD CONSTRAINT "base_station_base_station_map_fkey" FOREIGN KEY ("base_station_map") REFERENCES "public"."xy_map" ("map_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_beacon" ADD CONSTRAINT "beacon_beacon_location_object_fkey" FOREIGN KEY ("beacon_location_object") REFERENCES "public"."xy_location_object" ("location_object_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "xy_bind_lom" ADD CONSTRAINT "bind_lom_bind_lom_location_object_fkey" FOREIGN KEY ("bind_lom_location_object") REFERENCES "public"."xy_location_object" ("location_object_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_bind_lom" ADD CONSTRAINT "bind_lom_bind_lom_map_fkey" FOREIGN KEY ("bind_lom_map") REFERENCES "public"."xy_map" ("map_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_fence" ADD CONSTRAINT "xy_fence_fence_map_fkey" FOREIGN KEY ("fence_map") REFERENCES "public"."xy_map" ("map_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "xy_local_server" ADD CONSTRAINT "local_server_local_server_building_fkey" FOREIGN KEY ("local_server_building") REFERENCES "public"."xy_building" ("building_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "xy_location_object" ADD CONSTRAINT "location_object_location_object_beacon_fkey" FOREIGN KEY ("location_object_beacon") REFERENCES "public"."xy_beacon" ("beacon_id") ON DELETE SET NULL ON UPDATE CASCADE;
ALTER TABLE "xy_map" ADD CONSTRAINT "map_map_local_server_fkey" FOREIGN KEY ("map_local_server") REFERENCES "public"."xy_local_server" ("local_server_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER SEQUENCE "base_station_base_station_id_seq"
OWNED BY "xy_base_station"."base_station_id";
SELECT setval('"base_station_base_station_id_seq"', 1, false);
ALTER SEQUENCE "base_station_base_station_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "beacon_beacon_id_seq"
OWNED BY "xy_beacon"."beacon_id";
SELECT setval('"beacon_beacon_id_seq"', 1, false);
ALTER SEQUENCE "beacon_beacon_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "building_building_id_seq"
OWNED BY "xy_building"."building_id";
SELECT setval('"building_building_id_seq"', 1, false);
ALTER SEQUENCE "building_building_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "config_config_id_seq"
OWNED BY "xy_config"."config_id";
SELECT setval('"config_config_id_seq"', 2, true);
ALTER SEQUENCE "config_config_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "local_server_local_server_id_seq"
OWNED BY "xy_local_server"."local_server_id";
SELECT setval('"local_server_local_server_id_seq"', 1, false);
ALTER SEQUENCE "local_server_local_server_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "location_object_location_object_id_seq"
OWNED BY "xy_location_object"."location_object_id";
SELECT setval('"location_object_location_object_id_seq"', 1, false);
ALTER SEQUENCE "location_object_location_object_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "map_map_id_seq"
OWNED BY "xy_map"."map_id";
SELECT setval('"map_map_id_seq"', 1, false);
ALTER SEQUENCE "map_map_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "xy_alarm_alarm_id_seq"
OWNED BY "xy_alarm"."alarm_id";
SELECT setval('"xy_alarm_alarm_id_seq"', 1, false);
ALTER SEQUENCE "xy_alarm_alarm_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "xy_fence_fence_id_seq"
OWNED BY "xy_fence"."fence_id";
SELECT setval('"xy_fence_fence_id_seq"', 1, false);
ALTER SEQUENCE "xy_fence_fence_id_seq" OWNER TO "postgres";
ALTER SEQUENCE "xy_user_user_id_seq"
OWNED BY "xy_user"."user_id";
SELECT setval('"xy_user_user_id_seq"', 1, false);
ALTER SEQUENCE "xy_user_user_id_seq" OWNER TO "postgres";
