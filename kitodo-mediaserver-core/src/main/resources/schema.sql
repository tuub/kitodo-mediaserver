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
  id varchar(100) NOT NULL,
  title varchar(255) NOT NULL,
  path varchar(255) NOT NULL,
  enabled tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY(id)
);

CREATE TABLE identifier (
  identifier varchar(100) NOT NULL,
  type varchar(50),
  work_id varchar(100) NOT NULL,
  PRIMARY KEY (identifier),
  FOREIGN KEY (work_id) REFERENCES work(id)
);

CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(60) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY(`username`)
);
