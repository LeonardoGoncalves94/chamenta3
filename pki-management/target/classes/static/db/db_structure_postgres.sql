
--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS role;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE role_seq;

CREATE TABLE role (
  role_id int NOT NULL DEFAULT NEXTVAL ('role_seq'),
  role varchar(255) DEFAULT NULL,
  PRIMARY KEY (role_id)
)  ;

ALTER SEQUENCE role_seq RESTART WITH 2;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `creditor_user`
--

DROP TABLE IF EXISTS creditor_user;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE SEQUENCE user_seq;

CREATE TABLE creditor_user (
  user_id int NOT NULL DEFAULT NEXTVAL ('user_seq'),
  active int DEFAULT NULL,
  email varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  debtor_bank_id int,
  PRIMARY KEY (user_id),
  KEY FK_debtor_bank (debtor_bank_id),
  CONSTRAINT FK_debtor_bank FOREIGN KEY (debtor_bank_id) REFERENCES debtor_bank (bank_id)
)  ;

ALTER SEQUENCE user_seq RESTART WITH 4;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS user_role;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE user_role (
  user_id int NOT NULL,
  role_id int NOT NULL,
  PRIMARY KEY (user_id,role_id)
 ,
  CONSTRAINT FK859n2jvi8ivhui0rl0esws6o FOREIGN KEY (user_id) REFERENCES creditor_user (user_id),
  CONSTRAINT FKa68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES role (role_id)
) ;

CREATE INDEX FKa68196081fvovjhkek5m97n3y ON user_role (role_id);

/* CReditor conf table*/
CREATE TABLE creditor_conf (
  creditor_id varchar(50) NOT NULL,
  creditor_name varchar(255) NOT NULL,
  creditor_address varchar(255),
  creditor_country varchar(255) NOT NULL,
  creditor_bank_bic varchar(15) NOT NULL,
  PRIMARY KEY (creditor_id)
)  ;

CREATE SEQUENCE debtor_bank_seq;

CREATE TABLE debtor_bank (
  bank_id int NOT NULL DEFAULT NEXTVAL ('debtor_bank_seq'),
  bank_bic varchar(20) NOT NULL,
  bank_name varchar(255) NOT NULL,
  approval_type varchar(10) NOT NULL,
  debtor_bank_id int,
  PRIMARY KEY (bank_id)
) ;


create unique index bank_bic_unique_idx on debtor_bank (bank_bic);

ALTER SEQUENCE debtor_bank_seq RESTART WITH 4;
