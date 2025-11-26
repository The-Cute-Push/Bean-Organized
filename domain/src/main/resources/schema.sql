CREATE TABLE IF NOT EXISTS tb_user (
    id BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_task (
    id BIGSERIAL PRIMARY KEY,
    id_user BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_end TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pendente' NOT NULL,

    CONSTRAINT chk_status_valid CHECK (status IN ('pending', 'in_progress', 'completed')),
    CONSTRAINT fk_task_user FOREIGN KEY (id_user) REFERENCES tb_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_reminder (
    id BIGSERIAL PRIMARY KEY,
    id_task BIGINT NOT NULL,
    date_hour TIMESTAMP NOT NULL,
    sent BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_reminder_task FOREIGN KEY (id_task) REFERENCES tb_task(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_category (
    id BIGSERIAL PRIMARY KEY,
    id_user BIGINT NOT NULL,
    "name" VARCHAR(50) NOT NULL,
    color VARCHAR(20),
    CONSTRAINT fk_category_user FOREIGN KEY (id_user) REFERENCES tb_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_category_task (
    id_category BIGINT NOT NULL,
    id_task BIGINT NOT NULL,
    PRIMARY KEY (id_category, id_task),
    CONSTRAINT fk_pivot_category FOREIGN KEY (id_category) REFERENCES tb_category(id) ON DELETE CASCADE,
    CONSTRAINT fk_pivot_task FOREIGN KEY (id_task) REFERENCES tb_task(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reminder_pendentes ON tb_reminder(sent, date_hour);
CREATE INDEX IF NOT EXISTS idx_tarefas_user ON tb_task(id_user);