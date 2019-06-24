
# CREATE SCHEMA IF NOT EXISTS `ob_tn01`;

USE `ob_tn01`;

CREATE TABLE `user` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `api_user_id` VARCHAR(128) NOT NULL,
  `psp_user_id` VARCHAR(128) NOT NULL,
  `active`      BOOLEAN      NOT NULL,
  CONSTRAINT `pk_user` PRIMARY KEY (id),
  CONSTRAINT `uk_user.api_user` UNIQUE (`api_user_id`),
  CONSTRAINT `uk_user.psp_user` UNIQUE (`psp_user_id`)
);

CREATE TABLE `consent` (
  `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id`       VARCHAR(128) NOT NULL,
  `scope`            VARCHAR(8)   NOT NULL,
  `client_id`        VARCHAR(128) NOT NULL,
  `user_id`          BIGINT(20)   NULL     DEFAULT NULL,
  `status`           VARCHAR(128) NOT NULL,
  `transaction_id`   VARCHAR(128) NULL     DEFAULT NULL,
  `created_on`       TIMESTAMP(3) NOT NULL,
  `updated_on`       TIMESTAMP(3) NULL     DEFAULT NULL,
  `expires_on`       TIMESTAMP(3) NULL     DEFAULT NULL,
  `transaction_from` TIMESTAMP(3) NULL     DEFAULT NULL,
  `transaction_to`   TIMESTAMP(3) NULL     DEFAULT NULL,
  CONSTRAINT `pk_consent` PRIMARY KEY (id),
  CONSTRAINT `uk_consent.consent` UNIQUE (`consent_id`),
#   CONSTRAINT `uk_consent.scope` UNIQUE (`scope`, `client_id`, `user_id`, `status`),
  CONSTRAINT `fk_consent.user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `consent_event` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id`  BIGINT(20)   NOT NULL,
  `action`      VARCHAR(32)  NOT NULL,
  `created_on`  TIMESTAMP(3) NOT NULL,
  `seq_no`      INT          NOT NULL,
  `reason_code` VARCHAR(64)  NULL     DEFAULT NULL,
  `reason_desc` VARCHAR(256) NULL     DEFAULT NULL,
  CONSTRAINT `pk_consent_event` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_event.seq` UNIQUE (`seq_no`),
  CONSTRAINT `fk_consent_event.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`)
);

CREATE TABLE `consent_account` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id` BIGINT(20)   NOT NULL,
  `account_id` VARCHAR(128) NOT NULL,
  `event_id`   BIGINT(20)   NOT NULL,
  CONSTRAINT `pk_consent` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_account.account` UNIQUE (`consent_id`, `account_id`),
  CONSTRAINT `fk_consent_account.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `fk_consent_account.event` FOREIGN KEY (`event_id`) REFERENCES `consent_event` (`id`)
);

CREATE TABLE `consent_permission` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id` BIGINT(20)   NOT NULL,
  `permission` VARCHAR(128) NOT NULL,
  `event_id`   BIGINT(20)   NOT NULL,
  CONSTRAINT `pk_consent_permission` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_permission.permission` UNIQUE (`consent_id`, `permission`),
  CONSTRAINT `fk_consent_permission.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `fk_consent_permission.event` FOREIGN KEY (`event_id`) REFERENCES `consent_event` (`id`)
);

CREATE TABLE `consent_status_step` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id` BIGINT(20)   NOT NULL,
  `status`     VARCHAR(128) NOT NULL,
  `event_id`   BIGINT(20)   NOT NULL,
  `seq_no`     INT          NOT NULL,
  CONSTRAINT `pk_consent_status_step` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_status_step.seq` UNIQUE (`consent_id`, `seq_no`),
  CONSTRAINT `uk_consent_status_step.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `uk_consent_status_step.event` FOREIGN KEY (`event_id`) REFERENCES `consent_event` (`id`)
);

CREATE TABLE `consent_transaction` (
  `id`             BIGINT(20)     NOT NULL AUTO_INCREMENT,
  `consent_id`     BIGINT(20)     NOT NULL,
  `transaction_id` VARCHAR(128)   NOT NULL,
  `client_ref_id`  VARCHAR(128)   NOT NULL,
  `amount`         NUMERIC(22, 4) NOT NULL,
  `currency`       CHAR(3)        NOT NULL,
  `amount_type`    VARCHAR(32)    NOT NULL,
  `scenario`       VARCHAR(32)    NOT NULL,
  `initiator`      VARCHAR(32)    NOT NULL,
  `initiator_type` VARCHAR(32)    NULL     DEFAULT NULL,
  `payer_id_type`  VARCHAR(32)    NOT NULL,
  `payer_id`       VARCHAR(128)   NOT NULL,
  `payee_id_type`  VARCHAR(32)    NOT NULL,
  `payee_id`       VARCHAR(128)   NOT NULL,
  `status`         VARCHAR(128)   NOT NULL,
  `event_id`       BIGINT(20)     NOT NULL,
  `seq_no`         INT            NOT NULL,
  `performed_on`   TIMESTAMP(3)   NULL     DEFAULT NULL,
  `expires_on`     TIMESTAMP(3)   NULL     DEFAULT NULL,
  `note`           VARCHAR(128)   NULL     DEFAULT NULL,
  `error_code`     VARCHAR(64)    NULL     DEFAULT NULL,
  `error_desc`     VARCHAR(256)   NULL     DEFAULT NULL,
  CONSTRAINT `pk_consent_transaction` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_transaction.seq` UNIQUE (`consent_id`, `seq_no`),
  CONSTRAINT `uk_consent_transaction.transaction` UNIQUE (`transaction_id`),
  CONSTRAINT `uk_consent_transaction.client_ref` UNIQUE (`client_ref_id`),
  CONSTRAINT `uk_consent_transaction.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `uk_consent_transaction.event` FOREIGN KEY (`event_id`) REFERENCES `consent_event` (`id`)
);