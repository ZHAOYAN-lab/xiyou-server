/*
 Navicat Premium Data Transfer

 Source Server         : 10.8.1.80
 Source Server Type    : PostgreSQL
 Source Server Version : 150004 (150004)
 Source Host           : 10.8.1.80:5432
 Source Catalog        : xiyou_mqtt_auth
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 150004 (150004)
 File Encoding         : 65001

 Date: 24/08/2023 02:46:35
*/


-- ----------------------------
-- Type structure for action
-- ----------------------------
DROP TYPE IF EXISTS "public"."action";
CREATE TYPE "public"."action" AS ENUM (
  'publish',
  'subscribe',
  'all'
);
ALTER TYPE "public"."action" OWNER TO "postgres";

-- ----------------------------
-- Type structure for permission
-- ----------------------------
DROP TYPE IF EXISTS "public"."permission";
CREATE TYPE "public"."permission" AS ENUM (
  'allow',
  'deny'
);
ALTER TYPE "public"."permission" OWNER TO "postgres";

-- ----------------------------
-- Sequence structure for mqtt_acl_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mqtt_acl_id_seq";
CREATE SEQUENCE "public"."mqtt_acl_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for mqtt_user_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."mqtt_user_user_id_seq";
CREATE SEQUENCE "public"."mqtt_user_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Table structure for mqtt_acl
-- ----------------------------
DROP TABLE IF EXISTS "public"."mqtt_acl";
CREATE TABLE "public"."mqtt_acl" (
  "id" int4 NOT NULL DEFAULT nextval('mqtt_acl_id_seq'::regclass),
  "ipaddress" varchar(60) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "username" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "clientid" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "action" "public"."action",
  "permission" "public"."permission",
  "topic" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;

-- ----------------------------
-- Records of mqtt_acl
-- ----------------------------
INSERT INTO "public"."mqtt_acl" VALUES (4, '', 'device', '', 'all', 'deny', '#');
INSERT INTO "public"."mqtt_acl" VALUES (2, '', 'device', '', 'publish', 'allow', '/ifengniao/cloud/mqtt/xiyou/server/cloud');
INSERT INTO "public"."mqtt_acl" VALUES (1, '', 'device', '', 'all', 'allow', '/ifengniao/cloud/mqtt/xiyou/server/public');
INSERT INTO "public"."mqtt_acl" VALUES (3, '', 'device', '', 'subscribe', 'allow', '/ifengniao/cloud/mqtt/xiyou/server/local/+');
INSERT INTO "public"."mqtt_acl" VALUES (5, '', 'xiyou_page', '', 'subscribe', 'allow', '/ifengniao/cloud/mqtt/xiyou/page/beacons/bymap/+');
INSERT INTO "public"."mqtt_acl" VALUES (6, '', 'xiyou_page', '', 'all', 'deny', '#');

-- ----------------------------
-- Table structure for mqtt_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."mqtt_user";
CREATE TABLE "public"."mqtt_user" (
  "id" int4 NOT NULL DEFAULT nextval('mqtt_user_user_id_seq'::regclass),
  "username" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "password_hash" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "salt" varchar(40) COLLATE "pg_catalog"."default" NOT NULL,
  "is_superuser" bool NOT NULL
)
;

-- ----------------------------
-- Records of mqtt_user
-- ----------------------------
INSERT INTO "public"."mqtt_user" VALUES (1, 'admin', 'xiyou0#00Admin', '', 't');
INSERT INTO "public"."mqtt_user" VALUES (2, 'device', '_xiyou_Device_1234_', '', 'f');
INSERT INTO "public"."mqtt_user" VALUES (3, 'xiyou_page', 'xiyou_page_123', '', 'f');

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mqtt_acl_id_seq"
OWNED BY "public"."mqtt_acl"."id";
SELECT setval('"public"."mqtt_acl_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."mqtt_user_user_id_seq"
OWNED BY "public"."mqtt_user"."id";
SELECT setval('"public"."mqtt_user_user_id_seq"', 3, true);

-- ----------------------------
-- Primary Key structure for table mqtt_acl
-- ----------------------------
ALTER TABLE "public"."mqtt_acl" ADD CONSTRAINT "mqtt_acl_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table mqtt_user
-- ----------------------------
ALTER TABLE "public"."mqtt_user" ADD CONSTRAINT "mqtt_user_pkey" PRIMARY KEY ("id");
