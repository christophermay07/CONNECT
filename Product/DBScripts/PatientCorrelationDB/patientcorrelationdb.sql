-- MySQL dump 10.11
--
-- Host: localhost    Database: patientcorrelationdb
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt

--
-- Table structure for table `patientcorrelationdb.correlatedidentifiers`
--

DROP DATABASE IF EXISTS patientcorrelationdb;
FLUSH PRIVILEGES;

CREATE DATABASE patientcorrelationdb;

CREATE TABLE IF NOT EXISTS patientcorrelationdb.correlatedidentifiers (
  correlationId int(10) unsigned NOT NULL auto_increment,
  PatientAssigningAuthorityId varchar(64) NOT NULL,
  PatientId varchar(128) NOT NULL,
  CorrelatedPatientAssignAuthId varchar(64) NOT NULL,
  CorrelatedPatientId varchar(128) NOT NULL,
  CorrelationExpirationDate datetime,
  PRIMARY KEY  (correlationId)
);

CREATE TABLE IF NOT EXISTS patientcorrelationdb.pddeferredcorrelation (
  Id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  MessageId VARCHAR(100) NOT NULL,
  AssigningAuthorityId varchar(64) NOT NULL,
  PatientId varchar(128) NOT NULL,
  CreationTime DATETIME NOT NULL,
  PRIMARY KEY (Id)
);

GRANT SELECT,INSERT,UPDATE,DELETE ON patientcorrelationdb.* to nhincuser;
