--
-- Host: localhost    Database: assigningauthoritydb
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt\

--
-- Table structure for table `aa_to_home_community_mapping`
--

DROP DATABASE IF EXISTS assigningauthoritydb;
FLUSH PRIVILEGES;

CREATE DATABASE assigningauthoritydb;

CREATE TABLE IF NOT EXISTS assigningauthoritydb.aa_to_home_community_mapping (
  id int(10) unsigned NOT NULL auto_increment,
  assigningauthorityid varchar(64) NOT NULL,
  homecommunityid varchar(64) NOT NULL,
  PRIMARY KEY  (id,assigningauthorityid)
);

GRANT SELECT,INSERT,UPDATE,DELETE ON assigningauthoritydb.* to nhincuser;
