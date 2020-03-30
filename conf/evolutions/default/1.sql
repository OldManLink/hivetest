# hivetest schema

# --- !Ups

CREATE TABLE `client`
(
    `id`               int(11) unsigned NOT NULL AUTO_INCREMENT,
    `name`             text,
    `creation_instant` DATETIME         NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `cpu_log`
(
    `id`               int(11) unsigned NOT NULL AUTO_INCREMENT,
    `client_id`        int(11) unsigned NOT NULL,
    `sequence`         int(11) unsigned NOT NULL,
    `cpu_percent`      tinyint          NOT NULL,
    `creation_instant` DATETIME         NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`client_id`)
        REFERENCES `client` (`id`)
        ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# --- !Downs
DROP TABLE `cpu_log`;
DROP TABLE `client`;
