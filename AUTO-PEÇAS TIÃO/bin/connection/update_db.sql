-- Atualização para suportar orçamentos com múltiplos itens
ALTER TABLE pre_vendas ADD COLUMN id_orcamento INT DEFAULT NULL;

-- Garantir que a tabela vendas_concluidas suporte referência ao orçamento se necessário
-- (Por enquanto vamos manter id_prevenda para não quebrar o histórico, mas a lógica mudará no Java)
