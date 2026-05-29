-- Correção 1: Tabela pedidos_compra (referenciada pelo PedidoCompraDAO mas ausente no banco original)
CREATE TABLE IF NOT EXISTS `pedidos_compra` (
  `id_pedido`    int NOT NULL AUTO_INCREMENT,
  `fornecedor`   varchar(100) NOT NULL,
  `nome_peca`    varchar(100) NOT NULL,
  `quantidade`   int NOT NULL,
  `id_gerente`   int NOT NULL,
  `data_pedido`  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pedido`),
  KEY `id_gerente` (`id_gerente`),
  CONSTRAINT `pedidos_compra_ibfk_1`
    FOREIGN KEY (`id_gerente`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
