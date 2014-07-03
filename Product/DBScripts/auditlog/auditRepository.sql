DROP DATABASE IF EXISTS auditrepo;
FLUSH PRIVILEGES;

CREATE DATABASE auditrepo;

CREATE TABLE IF NOT EXISTS auditrepo.auditrepository
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    audit_timestamp DATETIME,
    eventId BIGINT NOT NULL,
    userId VARCHAR(100),
    participationTypeCode SMALLINT,
    participationTypeCodeRole SMALLINT,
    participationIDTypeCode VARCHAR(100),
    receiverPatientId VARCHAR(128),
    senderPatientId VARCHAR(128),
    communityId VARCHAR(255),
    messageType VARCHAR(100) NOT NULL,
    message LONGBLOB,
    PRIMARY KEY (id),
    UNIQUE UQ_eventlog_id(id)
);

GRANT SELECT,INSERT,UPDATE,DELETE ON auditrepo.* to nhincuser;
