DROP DATABASE IF EXISTS patientdb;
FLUSH PRIVILEGES;

CREATE DATABASE patientdb;

-- -----------------------------------------------------
-- Table `patientdb`.`patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS patientdb.patient (
  patientId BIGINT NOT NULL AUTO_INCREMENT,
  dateOfBirth DATE NULL,
  gender CHAR(2) NULL,
  ssn CHAR(9) NULL,
  PRIMARY KEY (patientId),
  UNIQUE INDEX patientId_UNIQUE (patientId ASC) )
COMMENT = 'Patient Repository';

-- -----------------------------------------------------
-- Table `patientdb`.`identifier`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS patientdb.identifier (
  identifierId BIGINT NOT NULL AUTO_INCREMENT,
  patientId BIGINT NOT NULL,
  id VARCHAR(64) NULL,
  organizationId VARCHAR(64) NULL,
  PRIMARY KEY (identifierId),
  UNIQUE INDEX identifierrId_UNIQUE (identifierId ASC),
  INDEX fk_identifier_patient (patientId ASC),
  CONSTRAINT fk_identifier_patient
    FOREIGN KEY (patientId )
    REFERENCES patientdb.patient (patientId )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Identifier definitions';

-- -----------------------------------------------------
-- Table `patientdb`.`personname`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS patientdb.personname (
  personnameId BIGINT NOT NULL AUTO_INCREMENT,
  patientId BIGINT NOT NULL,
  prefix VARCHAR(64) NULL,
  firstName VARCHAR(64) NULL,
  middleName VARCHAR(64) NULL,
  lastName VARCHAR(64) NULL,
  suffix VARCHAR(64) NULL,
  PRIMARY KEY (personnameId),
  UNIQUE INDEX personnameId_UNIQUE (personnameId ASC),
  INDEX fk_personname_patient (patientId ASC),
  CONSTRAINT fk_personname_patient
    FOREIGN KEY (patientId )
    REFERENCES patientdb.patient (patientId )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Person Names';

-- -----------------------------------------------------
-- Table `patientdb`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS patientdb.address (
  addressId BIGINT NOT NULL AUTO_INCREMENT,
  patientId BIGINT NOT NULL,
  street1 VARCHAR(128) NULL,
  street2 VARCHAR(128) NULL,
  city VARCHAR(128) NULL,
  state VARCHAR(128) NULL,
  postal VARCHAR(45) NULL,
  PRIMARY KEY (addressId),
  UNIQUE INDEX addressId_UNIQUE (addressId ASC),
  INDEX fk_address_patient (patientId ASC),
  CONSTRAINT fk_address_patient
    FOREIGN KEY (patientId )
    REFERENCES patientdb.patient (patientId )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Addresses';

-- -----------------------------------------------------
-- Table `patientdb`.`phonenumber`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS patientdb.phonenumber (
  phonenumberId BIGINT NOT NULL AUTO_INCREMENT,
  patientId BIGINT NOT NULL,
  value VARCHAR(64) NULL,
  PRIMARY KEY (phonenumberId),
  UNIQUE INDEX phonenumberId_UNIQUE (phonenumberId ASC),
  INDEX fk_phonenumber_patient (patientId ASC),
  CONSTRAINT fk_phonenumber_patient
    FOREIGN KEY (patientId )
    REFERENCES patientdb.patient (patientId )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = 'Phone Numbers';

GRANT SELECT,INSERT,UPDATE,DELETE ON patientdb.* to nhincuser;
