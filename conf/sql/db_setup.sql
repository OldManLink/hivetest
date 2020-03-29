CREATE DATABASE IF NOT EXISTS `hivetest` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `hivetest`;

GRANT ALTER, CREATE, DELETE, DROP, INDEX, INSERT, SELECT, UPDATE ON hivetest.* TO `hivedbuser`@`localhost` IDENTIFIED BY 'streeemz';

GRANT ALTER, CREATE, DELETE, DROP, INDEX, INSERT, SELECT, UPDATE ON hivetest.* TO `hivedbuser`@`%`IDENTIFIED BY 'streeemz';

