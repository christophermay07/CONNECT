DROP DATABASE IF EXISTS assigningauthoritydb;
FLUSH PRIVILEGES;

CREATE DATABASE eventdb;

CREATE TABLE IF NOT EXISTS eventdb.event (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  description longtext,
  transactionId VARCHAR(100),
  messageId VARCHAR(100),
  serviceType VARCHAR(100),
  initiatingHcid VARCHAR(100),
  respondingHcids VARCHAR(100),
  eventTime TIMESTAMP,
  PRIMARY KEY (id) )
COMMENT = 'Event Logging';

GRANT SELECT,INSERT,UPDATE,DELETE ON eventdb.* to nhincuser;
