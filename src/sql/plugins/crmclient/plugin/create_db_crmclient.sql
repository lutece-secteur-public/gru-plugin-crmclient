-- liquibase formatted sql
-- changeset crmclient:create_db_crmclient.sql
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Table structure for table crm_client_crm_queue
--
DROP TABLE IF EXISTS crm_client_crm_queue;
CREATE TABLE crm_client_crm_queue (
	id_crm_queue INT DEFAULT 0 NOT NULL,
	is_locked SMALLINT DEFAULT 0,
	PRIMARY KEY (id_crm_queue)
);

CREATE INDEX is_locked_crm_client_crm_queue ON crm_client_crm_queue (is_locked);

--
-- Table structure for table crm_client_crm_item
--
DROP TABLE IF EXISTS crm_client_crm_item;
CREATE TABLE crm_client_crm_item (
	id_crm_queue INT DEFAULT 0 NOT NULL,
	crm_item LONG VARBINARY,
	PRIMARY KEY (id_crm_queue)
);
