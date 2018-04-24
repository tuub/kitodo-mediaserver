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

-- Insert default user: admin/admin
INSERT INTO `user` (`username`, `password`, `name`, `enabled`) VALUES
  ('admin', '$2a$10$oxfH7pRjIZcyB97LJ7/eSeSBEQyUE65kBiOZYADJ4wFYPkD2YqMiG', 'Administrator', 1);
