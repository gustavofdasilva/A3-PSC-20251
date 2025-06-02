## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

CREATE ROLE 'a3psc_user','root';
CREATE DATABASE db_a3;

CREATE TABLE usuario (
    id int auto_increment not null,
    nome varchar(255) not null,
    email varchar(255) not null,
    cpf varchar(11) not null,
    telefone int not null,
    saldo int not null,
    id_agencia int not null,
    conta varchar(255) not null,
    banco varchar(255) not null,
    FOREIGN KEY (id_agencia) REFERENCES agencia(id),
    PRIMARY KEY (id)
);

CREATE TABLE chave_pix (
    id int auto_increment not null,
    usuario_id varchar(255) not null,
    tipo varchar(11) not null,
    chave varchar(255) not null,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    PRIMARY KEY (id)
);

CREATE TABLE operacao (
	id int auto_increment not null,
    dt_operacao timestamp not null,
    tipo varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE deposito (
	id int not null,
	id_usuario int not null,
    novo_saldo int not null,
    valor_depositado int not null,
    FOREIGN KEY (id) REFERENCES operacao(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

CREATE TABLE saque (
	id int not null,
	id_usuario int not null,
    novo_saldo int not null,
    valor_sacado int not null,
    FOREIGN KEY (id) REFERENCES operacao(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

CREATE TABLE transferencia (
	id int not null,
	id_usuario_remetente int not null,
    id_usuario_destinatario int not null,
    FOREIGN KEY (id) REFERENCES operacao(id),
    FOREIGN KEY (id_usuario_remetente) REFERENCES usuario(id),
    FOREIGN KEY (id_usuario_destinatario) REFERENCES usuario(id),
    PRIMARY KEY (id)
);

CREATE TABLE agencia (
	id int auto_increment not null,
    id_banco int not null,
    endereco varchar(255) null,
    FOREIGN KEY (id_banco) REFERENCES banco(id),
    PRIMARY KEY (id)
);

CREATE TABLE banco (
	id int auto_increment not null,
    nome varchar(255) not null,
    PRIMARY KEY (id)
);
