--
-- (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
--
-- This file is part of the Kitodo project.
--
-- It is licensed under GNU General Public License version 3 or later.
--
-- For the full copyright and license information, please read the
-- LICENSE file that was distributed with this source code.
--

CREATE TABLE work (
  id varchar(255) NOT NULL,
  title varchar(255) NOT NULL,
  path varchar(255) NOT NULL,
  enabled tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY(id)
);

CREATE TABLE identifier (
  identifier varchar(255) NOT NULL,
  type varchar(255),
  work_id varchar(255) NOT NULL,
  PRIMARY KEY (identifier),
  FOREIGN KEY (work_id) REFERENCES work(id)
);

CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(60) NOT NULL,
  `name` varchar(255) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY(`username`)
);

CREATE TABLE `action_data` (
  `id` int NOT NULL AUTO_INCREMENT,
  `work_id` varchar(255) NOT NULL,
  `action_name` varchar(255) NOT NULL,
  `request_time` TIMESTAMP NULL DEFAULT NULL,
  `start_time` TIMESTAMP NULL DEFAULT NULL,
  `end_time` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY(`id`),
  FOREIGN KEY (`work_id`) REFERENCES `work`(`id`)
);

CREATE TABLE `action_parameter` (
  `action_data_id` int NOT NULL,
  `parameter_key` varchar(255) NOT NULL,
  `parameter` varchar(255) NOT NULL,
  PRIMARY KEY(`action_data_id`, `parameter_key`),
  FOREIGN KEY (`action_data_id`) REFERENCES `action_data`(`id`)
);

