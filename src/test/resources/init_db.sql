CREATE USER develop@localhost IDENTIFIED BY '1234';
GRANT ALL ON active_model_shared_test.* TO develop@localhost;
CREATE DATABASE IF NOT EXISTS active_model_shared_test;