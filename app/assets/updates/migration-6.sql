CREATE TABLE IF NOT EXISTS `form_values_upgrade` (`_id` INTEGER PRIMARY KEY AUTOINCREMENT , `formItem_id` INTEGER , `lastModified` BIGINT , `session_id` BIGINT , `value` VARCHAR );
INSERT INTO `form_values_upgrade` (`formItem_id`, `lastModified`, `session_id`, `value`) SELECT `formItem_id`, `lastModified`, `session_id`, `value` FROM `form_values`;
DROP TABLE `form_values`;
ALTER TABLE `form_values_upgrade` RENAME TO `form_values`;