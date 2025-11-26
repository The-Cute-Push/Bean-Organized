CREATE TABLE IF NOT EXISTS tb_users (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_login TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_tarefas (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_vencimento TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pendente' NOT NULL,

    CONSTRAINT chk_status_valido CHECK (status IN ('pendente', 'em_andamento', 'concluida')),
    CONSTRAINT fk_tarefas_usuario FOREIGN KEY (id_usuario) REFERENCES tb_users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_lembretes (
    id BIGSERIAL PRIMARY KEY,
    id_tarefa BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    enviado BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_lembretes_tarefa FOREIGN KEY (id_tarefa) REFERENCES tb_tarefas(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_categorias (
    id BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cor VARCHAR(20),
    CONSTRAINT fk_categoria_usuario FOREIGN KEY (id_usuario) REFERENCES tb_users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tb_categoria_tarefa (
    id_categoria BIGINT NOT NULL,
    id_tarefa BIGINT NOT NULL,
    PRIMARY KEY (id_categoria, id_tarefa),
    CONSTRAINT fk_pivo_categoria FOREIGN KEY (id_categoria) REFERENCES tb_categorias(id) ON DELETE CASCADE,
    CONSTRAINT fk_pivo_tarefa FOREIGN KEY (id_tarefa) REFERENCES tb_tarefas(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_lembretes_pendentes ON tb_lembretes(enviado, data_hora);
CREATE INDEX IF NOT EXISTS idx_tarefas_usuario ON tb_tarefas(id_usuario);