# CREATE SCHEMA IF NOT EXISTS `ob_tn02`;

USE `ob_tn02`;

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
  `scope_code`       VARCHAR(8)   NOT NULL,
  `client_id`        VARCHAR(128) NOT NULL,
  `user_id`          BIGINT(20)   NULL     DEFAULT NULL,
  `status_code`      VARCHAR(128) NOT NULL,
  `created_on`       TIMESTAMP(3) NOT NULL,
  `updated_on`       TIMESTAMP(3) NULL     DEFAULT NULL,
  `expires_on`       TIMESTAMP(3) NULL     DEFAULT NULL,
  `transaction_from` TIMESTAMP(3) NULL     DEFAULT NULL,
  `transaction_to`   TIMESTAMP(3) NULL     DEFAULT NULL,
  CONSTRAINT `pk_consent` PRIMARY KEY (id),
  CONSTRAINT `uk_consent.consent` UNIQUE (`consent_id`),
  CONSTRAINT `fk_consent.user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `consent_event` (
  `id`                  BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id`          BIGINT(20)   NOT NULL,
  `action_code`         VARCHAR(32)  NOT NULL,
  `status_code`         VARCHAR(32)  NOT NULL,
  `resource_id`         VARCHAR(128) NULL,
  `created_on`          TIMESTAMP(3) NOT NULL,
  `seq_no`              BIGINT       NOT NULL,
  `consent_status_code` VARCHAR(128) NULL     DEFAULT NULL,
  `cause_id`            BIGINT(20)   NULL     DEFAULT NULL,
  `reason`              VARCHAR(64)  NULL     DEFAULT NULL,
  `reason_desc`         VARCHAR(256) NULL     DEFAULT NULL,
  CONSTRAINT `pk_consent_event` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_event.seq` UNIQUE (`seq_no`),
  CONSTRAINT `fk_consent_event.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `fk_consent_event.cause` FOREIGN KEY (`cause_id`) REFERENCES `consent_event` (`id`)
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
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `consent_id`      BIGINT(20)   NOT NULL,
  `permission_code` VARCHAR(128) NOT NULL,
  `event_id`        BIGINT(20)   NOT NULL,
  CONSTRAINT `pk_consent_permission` PRIMARY KEY (id),
  CONSTRAINT `uk_consent_permission.permission` UNIQUE (`consent_id`, `permission_code`),
  CONSTRAINT `fk_consent_permission.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `fk_consent_permission.event` FOREIGN KEY (`event_id`) REFERENCES `consent_event` (`id`)
);

CREATE TABLE `account_identification` (
  `id`                       BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `scheme_code`              VARCHAR(40)  NOT NULL,
  `identification`           VARCHAR(256) NOT NULL,
  `secondary_identification` VARCHAR(128) NULL     DEFAULT NULL,
  `name`                     VARCHAR(70)  NULL     DEFAULT NULL,
  CONSTRAINT `pk_account_identification` PRIMARY KEY (id),
  CONSTRAINT `uk_account_identification.identification` UNIQUE (`scheme_code`, `identification`, `secondary_identification`)
);

CREATE TABLE `address` (
  `id`                BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `address_type_code` VARCHAR(32) NULL     DEFAULT NULL,
  `department`        VARCHAR(70) NULL     DEFAULT NULL,
  `sub_department`    VARCHAR(70) NULL     DEFAULT NULL,
  `country`           CHAR(2)     NULL     DEFAULT NULL,
  `country_division1` VARCHAR(35) NULL     DEFAULT NULL,
  `country_division2` VARCHAR(35) NULL     DEFAULT NULL,
  `town`              VARCHAR(35) NULL     DEFAULT NULL,
  `postCode`          VARCHAR(16) NULL     DEFAULT NULL,
  `street`            VARCHAR(70) NULL     DEFAULT NULL,
  `building`          VARCHAR(16) NULL     DEFAULT NULL,
  CONSTRAINT `pk_address` PRIMARY KEY (id)
);

CREATE TABLE `address_line` (
  `id`         BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `address_id` BIGINT(20)  NOT NULL,
  `line`       VARCHAR(70) NOT NULL,
  CONSTRAINT `pk_address_line` PRIMARY KEY (id),
  CONSTRAINT `fk_address_line.address` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`)
);

CREATE TABLE `payment` (
  `id`                         BIGINT(20)     NOT NULL AUTO_INCREMENT,
  `consent_id`                 BIGINT(20)     NOT NULL,
  `payment_id`                 VARCHAR(40)    NOT NULL,
  `instruction_id`             VARCHAR(36)    NOT NULL,
  `end_to_end_id`              VARCHAR(36)    NOT NULL,
  `transaction_id`             VARCHAR(36)    NULL     DEFAULT NULL,
  `local_instrument_code`      VARCHAR(32)    NULL     DEFAULT NULL,
  `instructed_amount`          NUMERIC(23, 5) NOT NULL,
  `currency`                   CHAR(3)        NOT NULL,
  `debtor_identification_id`   BIGINT(20)     NULL,
  `creditor_identification_id` BIGINT(20)     NOT NULL,
  `creditor_address_id`        BIGINT(20)     NULL,
  `status_code`                VARCHAR(128)   NOT NULL,
  `created_on`                 TIMESTAMP(3)   NOT NULL,
  `updated_on`                 TIMESTAMP(3)   NULL     DEFAULT NULL,
  `expires_on`                 TIMESTAMP(3)   NULL     DEFAULT NULL,
  `expected_execution_on`      TIMESTAMP(3)   NULL     DEFAULT NULL,
  `expected_settlement_on`     TIMESTAMP(3)   NULL     DEFAULT NULL,
  `performed_on`               TIMESTAMP(3)   NULL     DEFAULT NULL,
  CONSTRAINT `pk_payment` PRIMARY KEY (id),
  CONSTRAINT `uk_payment.payment` UNIQUE (`payment_id`),
  CONSTRAINT `uk_payment.instruction` UNIQUE (`instruction_id`),
  CONSTRAINT `uk_payment.endtoend` UNIQUE (`end_to_end_id`),
  CONSTRAINT `uk_payment.consent` UNIQUE (`consent_id`),
  CONSTRAINT `uk_payment.address` UNIQUE (`creditor_address_id`),
  CONSTRAINT `fk_payment.consent` FOREIGN KEY (`consent_id`) REFERENCES `consent` (`id`),
  CONSTRAINT `fk_payment.debtor` FOREIGN KEY (`debtor_identification_id`) REFERENCES `account_identification` (`id`),
  CONSTRAINT `fk_payment.creditor` FOREIGN KEY (`creditor_identification_id`) REFERENCES `account_identification` (`id`),
  CONSTRAINT `fk_payment.address` FOREIGN KEY (`creditor_address_id`) REFERENCES `address` (`id`)
);

CREATE TABLE `payment_event` (
  `id`                  BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `payment_id`          BIGINT(20)   NOT NULL,
  `action_code`         VARCHAR(32)  NOT NULL,
  `status_code`         VARCHAR(32)  NOT NULL,
  `created_on`          TIMESTAMP(3) NOT NULL,
  `seq_no`              BIGINT       NOT NULL,
  `payment_status_code` VARCHAR(128) NULL     DEFAULT NULL,
  `cause_id`            BIGINT(20)   NULL     DEFAULT NULL,
  `reason`              VARCHAR(64)  NULL     DEFAULT NULL,
  `reason_desc`         VARCHAR(256) NULL     DEFAULT NULL,
  CONSTRAINT `pk_payment_event` PRIMARY KEY (id),
  CONSTRAINT `uk_payment_event.seq` UNIQUE (`seq_no`),
  CONSTRAINT `fk_payment_event.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
  CONSTRAINT `fk_payment_event.cause` FOREIGN KEY (`cause_id`) REFERENCES `consent_event` (`id`)
);

CREATE TABLE `payment_transfer` (
  `id`                    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `payment_id`            BIGINT(20)   NOT NULL,
  `transfer_id`           VARCHAR(36)  NOT NULL,
  `status_code`           VARCHAR(128) NOT NULL,
  `local_instrument_code` VARCHAR(32)  NULL     DEFAULT NULL,
  `transfer_status`       VARCHAR(128) NULL     DEFAULT NULL,
  `reason_code`           VARCHAR(32)  NULL     DEFAULT NULL,
  `reason_desc`           VARCHAR(256) NULL     DEFAULT NULL,
  `updated_on`            TIMESTAMP(3) NULL     DEFAULT NULL,
  `performed_on`          TIMESTAMP(3) NULL     DEFAULT NULL,
  CONSTRAINT `pk_payment_transfer` PRIMARY KEY (id),
  CONSTRAINT `uk_payment_transfer.transfer` UNIQUE (`transfer_id`),
  CONSTRAINT `fk_payment_transfer.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `payment_authorization` (
  `id`                      BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `payment_id`              BIGINT(20)   NOT NULL,
  `authorisation_type_code` VARCHAR(32)  NOT NULL,
  `status_code`             VARCHAR(32)  NOT NULL,
  `number_required`         SMALLINT     NULL     DEFAULT NULL,
  `number_received`         SMALLINT     NULL     DEFAULT NULL,
  `updated_on`              TIMESTAMP(3) NULL     DEFAULT NULL,
  `expires_on`              TIMESTAMP(3) NULL     DEFAULT NULL,
  CONSTRAINT `pk_payment_authorization` PRIMARY KEY (id),
  CONSTRAINT `uk_payment_authorization.payment` UNIQUE (`payment_id`),
  CONSTRAINT `fk_payment_authorization.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `charge` (
  `id`          BIGINT(20)     NOT NULL AUTO_INCREMENT,
  `payment_id`  BIGINT(20)     NOT NULL,
  `bearer_code` VARCHAR(32)    NOT NULL,
  `type_code`   VARCHAR(32)    NOT NULL,
  `amount`      NUMERIC(23, 5) NOT NULL,
  `currency`    CHAR(3)        NOT NULL,
  CONSTRAINT `pk_charge` PRIMARY KEY (id),
  CONSTRAINT `fk_charge.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `payment_risk` (
  `id`                               BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `payment_id`                       BIGINT(20)  NOT NULL,
  `payment_context_code`             VARCHAR(32) NULL     DEFAULT NULL,
  `merchant_category`                CHAR(4)     NULL     DEFAULT NULL,
  `merchant_customer_identification` VARCHAR(70) NULL     DEFAULT NULL,
  `delivery_address_id`              BIGINT(20)  NULL     DEFAULT NULL,
  CONSTRAINT `pk_payment_risk` PRIMARY KEY (id),
  CONSTRAINT `uk_payment_risk.payment` UNIQUE (`payment_id`),
  CONSTRAINT `uk_payment_risk.address` UNIQUE (`delivery_address_id`),
  CONSTRAINT `fk_payment_risk.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `remittance` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `payment_id`    BIGINT(20)   NOT NULL,
  `unstructured`  VARCHAR(140) NULL     DEFAULT NULL,
  `reference`     VARCHAR(35)  NULL     DEFAULT NULL,
  `supplementary` VARCHAR(256) NULL     DEFAULT NULL,
  CONSTRAINT `pk_remittance` PRIMARY KEY (id),
  CONSTRAINT `uk_remittance.payment` UNIQUE (`payment_id`),
  CONSTRAINT `fk_remittance.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `sca_support` (
  `id`                           BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `payment_id`                   BIGINT(20)   NOT NULL,
  `sca_exemption_code`           VARCHAR(32)  NULL     DEFAULT NULL,
  `authentication_approach_code` VARCHAR(32)  NULL     DEFAULT NULL,
  `reference_payment_id`         VARCHAR(128) NULL     DEFAULT NULL,
  CONSTRAINT `pk_sca_support` PRIMARY KEY (id),
  CONSTRAINT `uk_sca_support.payment` UNIQUE (`payment_id`),
  CONSTRAINT `fk_sca_support.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `interop_payment` (
  `id`                    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `payment_id`            BIGINT(20)   NOT NULL,
  `amount_type_code`      VARCHAR(32)  NOT NULL,
  `scenario_code`         VARCHAR(32)  NOT NULL,
  `subscenario_code`      VARCHAR(32)  NULL     DEFAULT NULL,
  `initiator_code`        VARCHAR(32)  NOT NULL,
  `initiator_type_code`   VARCHAR(32)  NOT NULL,
  `refund_transaction_id` VARCHAR(36)  NULL     DEFAULT NULL,
  `refund_reason`         VARCHAR(36)  NULL     DEFAULT NULL,
  `balance_of_payments`   CHAR(3)      NULL     DEFAULT NULL,
  `geo_longitude`         VARCHAR(32)  NULL     DEFAULT NULL,
  `geo_latitude`          VARCHAR(32)  NULL     DEFAULT NULL,
  `note`                  VARCHAR(128) NULL     DEFAULT NULL,
  CONSTRAINT `pk_interop_payment` PRIMARY KEY (id),
  CONSTRAINT `uk_interop_payment.payment` UNIQUE (`payment_id`),
  CONSTRAINT `fk_interop_payment.payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
);

CREATE TABLE `interop_extension` (
  `id`                 BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `interop_payment_id` BIGINT(20)  NOT NULL,
  `key`                VARCHAR(32) NOT NULL,
  `value`              VARCHAR(32) NOT NULL,
  CONSTRAINT `pk_interop_extension` PRIMARY KEY (id),
  CONSTRAINT `uk_interop_extension.payment` UNIQUE (`interop_payment_id`),
  CONSTRAINT `fk_interop_extension.payment` FOREIGN KEY (`interop_payment_id`) REFERENCES `interop_payment` (`id`)
);

CREATE TABLE `trusted_client`
(
    `id`                        BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `client_id`                 VARCHAR(128)   NOT NULL,
    `created_on`                TIMESTAMP(3) NOT NULL,
    `expires_on`                TIMESTAMP(3) NULL DEFAULT NULL,
    CONSTRAINT `pk_trusted_client` PRIMARY KEY (id),
    CONSTRAINT `uk_trusted_client.client` UNIQUE (`client_id`)
);

CREATE TABLE `trusted_beneficiary` (
  `id`                        BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `account_identification_id` BIGINT(20)   NOT NULL,
  `created_on`                TIMESTAMP(3) NOT NULL,
  `expires_on`                TIMESTAMP(3) NULL     DEFAULT NULL,
  CONSTRAINT `pk_trusted_beneficiary` PRIMARY KEY (id),
  CONSTRAINT `uk_trusted_beneficiary.account` UNIQUE (`account_identification_id`),
  CONSTRAINT `fk_trusted_beneficiary.account` FOREIGN KEY (`account_identification_id`) REFERENCES `account_identification` (`id`)
);

CREATE TABLE `trusted_user_beneficiary` (
  `id`                        BIGINT(20)     NOT NULL AUTO_INCREMENT,
  `client_id`                 VARCHAR(128)   NOT NULL,
  `user_id`                   BIGINT(20)     NOT NULL,
  `account_identification_id` BIGINT(20)     NOT NULL,
  `limit_amount`              NUMERIC(23, 5) NOT NULL,
  `created_on`                TIMESTAMP(3)   NOT NULL,
  `expires_on`                TIMESTAMP(3)   NULL     DEFAULT NULL,
  CONSTRAINT `pk_trusted_user_beneficiary` PRIMARY KEY (id),
  CONSTRAINT `uk_trusted_user_beneficiary.account` UNIQUE (`client_id`, `user_id`, `account_identification_id`),
  CONSTRAINT `fk_trusted_user_beneficiary.account` FOREIGN KEY (`account_identification_id`) REFERENCES `account_identification` (`id`)
);



