CREATE DATABASE IF NOT EXISTS db_a3;

USE db_a3;

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `cpf` varchar(11) DEFAULT NULL,
  `telefone` varchar(11) DEFAULT NULL,
  `saldo` double DEFAULT 0,
  `banco` varchar(255) NOT NULL,
  `senha` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `chave_pix`;
CREATE TABLE `chave_pix` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `tipo` varchar(11) NOT NULL,
  `chave` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `chave_pix_unique` (`chave`),
  CONSTRAINT `chave_pix_fk` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
);

DROP TABLE IF EXISTS `denuncia`;
CREATE TABLE `denuncia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario_denunciado` int(11) NOT NULL,
  `id_usuario_denunciando` int(11) NOT NULL,
  `dt_denuncia` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  CONSTRAINT `usuario_denunciado_FK` FOREIGN KEY (`id_usuario_denunciado`) REFERENCES `usuario` (`id`),
  CONSTRAINT `usuario_denunciando_FK` FOREIGN KEY (`id_usuario_denunciando`) REFERENCES `usuario` (`id`)
);

DROP TABLE IF EXISTS `operacao`;
CREATE TABLE `operacao` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dt_operacao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `id_usuario` int(11) NOT NULL,
  `tipo` enum('TRANSFERENCIA','DEPOSITO','SAQUE','TRANSFERENCIA_PIX','ESTORNO') NOT NULL,
  PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `transferencia`;
CREATE TABLE `transferencia` (
  `id` int(11) NOT NULL,
  `id_usuario_destinatario` int(11) NOT NULL,
  `quantia` double NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `transferencia_fk` FOREIGN KEY (`id`) REFERENCES `operacao` (`id`) ON DELETE CASCADE,
  CONSTRAINT `transferencia_fk_destinatario` FOREIGN KEY (`id_usuario_destinatario`) REFERENCES `usuario` (`id`) ON DELETE CASCADE
);

DROP TABLE IF EXISTS `deposito`;
CREATE TABLE `deposito` (
  `id` int(11) NOT NULL,
  `novo_saldo` double NOT NULL,
  `valor_depositado` double NOT NULL,
  CONSTRAINT `deposito_fk` FOREIGN KEY (`id`) REFERENCES `operacao` (`id`) ON DELETE CASCADE
);

DROP TABLE IF EXISTS `saque`;
CREATE TABLE `saque` (
  `id` int(11) NOT NULL,
  `novo_saldo` double NOT NULL,
  `valor_sacado` double NOT NULL,
  CONSTRAINT `saque_fk` FOREIGN KEY (`id`) REFERENCES `operacao` (`id`) ON DELETE CASCADE
);

DROP TABLE IF EXISTS `notificacao`;
CREATE TABLE `notificacao` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) DEFAULT NULL,
  `conteudo` varchar(255) DEFAULT NULL,
  `referencia` int(11) DEFAULT NULL COMMENT 'Qual id essa notificacao se referencia, normalmente ira se referenciar aos estornos de pix, mas nao necessariamente',
  `status` enum('NAO_LIDA','LIDA') NOT NULL DEFAULT 'NAO_LIDA',
  `tipo` enum('ESTORNO_PIX','OUTRO') NOT NULL DEFAULT 'OUTRO',
  `dt_criada` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  CONSTRAINT `notificacao_usuario_FK` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`)
);

DROP TABLE IF EXISTS `solicitacao_estorno_pix`;
CREATE TABLE `solicitacao_estorno_pix` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_transacao` int(11) NOT NULL,
  `id_usuario_solicitante` int(11) NOT NULL COMMENT 'ID do usuário que solicitou estorno (enviou o pix)',
  `id_usuario_solicitado` int(11) NOT NULL COMMENT 'ID do usuário que recebeu o pix',
  `dt_solicitacao` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('APROVADO','RECUSADO','AGUARDANDO') DEFAULT 'AGUARDANDO',
  PRIMARY KEY (`id`),
  CONSTRAINT `solicitacao_estorno_pix_transferencia_FK` FOREIGN KEY (`id_transacao`) REFERENCES `transferencia` (`id`),
  CONSTRAINT `solicitacao_estorno_pix_usuario_solicitado_FK` FOREIGN KEY (`id_usuario_solicitado`) REFERENCES `usuario` (`id`),
  CONSTRAINT `solicitacao_estorno_pix_usuario_solicitante_FK` FOREIGN KEY (`id_usuario_solicitante`) REFERENCES `usuario` (`id`)
);

