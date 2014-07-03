DROP DATABASE IF EXISTS messagemonitoringdb;
FLUSH PRIVILEGES;

CREATE DATABASE messagemonitoringdb;

CREATE TABLE IF NOT EXISTS messagemonitoringdb.monitoredmessage (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier',
  senderemailid varchar(255) DEFAULT NULL COMMENT 'sender email identifier',
  subject varchar(255) DEFAULT NULL COMMENT 'email Subject',
  messageid varchar(100) DEFAULT NULL COMMENT 'unique email message identifier',
  recipients varchar(4000) DEFAULT NULL,
  deliveryrequested tinyint(1) DEFAULT '0' COMMENT 'column to identify if the edge requested for delivery notification',
  status varchar(30) DEFAULT NULL COMMENT 'Pending, Completed, Error',
  createtime timestamp NULL DEFAULT NULL COMMENT 'Creation Time',
  updatetime timestamp NULL DEFAULT NULL COMMENT 'Record Update time',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS messagemonitoringdb.monitoredmessagenotification (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier',
  emailid varchar(255) NOT NULL COMMENT 'notification email identifier',
  messageid varchar(100) DEFAULT NULL COMMENT 'unique email message identifier',
  monitoredmessageid bigint(20) NOT NULL COMMENT 'unique trackmessage identifier',
  status varchar(30) NOT NULL COMMENT 'Pending, Completed, Error',
  createtime timestamp NULL DEFAULT NULL,
  updatetime timestamp NULL DEFAULT NULL,
  PRIMARY KEY (id),
  KEY fk_monitoredmessageId (monitoredmessageid),
  CONSTRAINT fk_monitoredmessageId FOREIGN KEY (monitoredmessageid) REFERENCES monitoredmessage (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Table to track outbound Message Monitoring notification';

GRANT SELECT,INSERT,UPDATE,DELETE ON messagemonitoringdb.* to nhincuser;
