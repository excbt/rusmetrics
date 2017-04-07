DROP TABLE portal.energy_passport_template CASCADE;
/*==============================================================*/
/* Table: energy_passport_template                              */
/*==============================================================*/
CREATE TABLE portal.energy_passport_template (
	id                      BIGINT DEFAULT nextval('seq_global_id') PRIMARY KEY,
	keyname                 TEXT NOT NULL,
	description             TEXT,
	document_name           TEXT,
	document_version        text,
	document_date           DATE DEFAULT LOCALtimestamp::DATE,
	version                 INT NOT NULL DEFAULT 0,
	deleted                 INT NOT NULL DEFAULT 0,
	created_date            TIMESTAMP without TIME zone NOT NULL DEFAULT now(),
	last_modified_date      TIMESTAMP without TIME zone,
	created_by              BIGINT,
	last_modified_by        BIGINT
	);

comment ON TABLE portal.energy_passport_template IS 'Шаблон энергетического паспорта';