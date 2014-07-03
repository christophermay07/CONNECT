DROP DATABASE IF EXISTS transrepo;
FLUSH PRIVILEGES;

CREATE DATABASE transrepo;

CREATE TABLE IF NOT EXISTS transrepo.transactionrepository (
    id BIGINT NOT NULL AUTO_INCREMENT,
    transactionId VARCHAR(100) NOT NULL,
    messageId VARCHAR(100) NOT NULL,
    transactionTime TIMESTAMP NULL,
    PRIMARY KEY (id),
    INDEX messageId_idx (messageId),
    UNIQUE transID_UNIQUE (transactionId, messageId) )
COMMENT = 'Message Transaction Repository';

GRANT SELECT,INSERT,UPDATE,DELETE ON transrepo.* to nhincuser;
