DROP TABLE DUPLICATES;
CREATE TABLE DUPLICATES (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  size INT DEFAULT NULL,
  path LONGTEXT DEFAULT NULL,
  name TEXT DEFAULT NULL,
  start_bytes VARCHAR(10000) DEFAULT NULL,
  checksum LONGTEXT DEFAULT NULL
);