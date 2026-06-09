INSERT INTO cliente (nome, email, telefone, endereco, ativo) VALUES ('João Silva', 'joao@email.com', '(11) 99999-1111', 'Rua A, 123 - São Paulo/SP', true);
INSERT INTO cliente (nome, email, telefone, endereco, ativo) VALUES ('Maria Santos', 'maria@email.com', '(11) 99999-2222', 'Rua B, 456 - São Paulo/SP', true);
INSERT INTO cliente (nome, email, telefone, endereco, ativo) VALUES ('Pedro Oliveira', 'pedro@email.com', '(11) 99999-3333', 'Rua C, 789 - São Paulo/SP', true);

INSERT INTO restaurante (nome, email, telefone, endereco, categoria, ativo, avaliacao, taxa_entrega) VALUES ('Pizzaria Bella', 'contato@pizzariabella.com', '(11) 3333-1111', 'Av. Paulista, 1000 - São Paulo/SP', 'Italiana', true, 5, 5.00);
INSERT INTO restaurante (nome, email, telefone, endereco, categoria, ativo, avaliacao, taxa_entrega) VALUES ('Burger House', 'contato@burgerhouse.com', '(11) 3333-2222', 'Rua Augusta, 500 - São Paulo/SP', 'Hamburgueria', true, 4, 3.50);
INSERT INTO restaurante (nome, email, telefone, endereco, categoria, ativo, avaliacao, taxa_entrega) VALUES ('Sushi Master', 'contato@sushimaster.com', '(11) 3333-3333', 'Rua Liberdade, 200 - São Paulo/SP', 'Japonesa', true, 5, 8.00);

INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 35.90, 'Pizza', true, 1, 1);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Pizza Calabresa', 'Molho de tomate, mussarela e calabresa', 38.90, 'Pizza', true, 1, 1);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Lasanha Bolonhesa', 'Lasanha tradicional com molho bolonhesa', 28.90, 'Massa', true, 2, 1);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('X-Burger', 'Hambúrguer, queijo, alface e tomate', 18.90, 'Hambúrguer', true, 1, 2);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('X-Bacon', 'Hambúrguer, queijo, bacon, alface e tomate', 22.90, 'Hambúrguer', true, 1, 2);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Batata Frita', 'Porção de batata frita crocante', 12.90, 'Acompanhamento', true, 1, 2);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Combo Sashimi', '15 peças de sashimi variado', 45.90, 'Sashimi', true, 1, 3);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Hot Roll Salmão', '8 peças de hot roll de salmão', 32.90, 'Hot Roll', true, 1, 3);
INSERT INTO produto (nome, descricao, preco, categoria, disponivel, validade, restaurante_id) VALUES ('Temaki Atum', 'Temaki de atum com cream cheese', 15.90, 'Temaki', true, 1, 3);

INSERT INTO pedido (data_pedido, entrega, sub_total, taxa_entrega, valor_total, numero_pedido, status, cliente_id, restaurante_id) VALUES (CURRENT_TIMESTAMP, true, 64.80, 5.00, 69.80, 'PED1234567890', 'PENDENTE', 1, 1);
INSERT INTO pedido (data_pedido, entrega, sub_total, taxa_entrega, valor_total, numero_pedido, status, cliente_id, restaurante_id) VALUES (CURRENT_TIMESTAMP, true, 41.80, 3.50, 45.30, 'PED1234567891', 'CONFIRMADO', 2, 2);
INSERT INTO pedido (data_pedido, entrega, sub_total, taxa_entrega, valor_total, numero_pedido, status, cliente_id, restaurante_id) VALUES (CURRENT_TIMESTAMP, true, 78.80, 8.00, 86.80, 'PED1234567892', 'ENTREGUE', 3, 3);

INSERT INTO item_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES (1, 35.90, 35.90, 1, 1);
INSERT INTO item_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES (1, 28.90, 28.90, 1, 3);
INSERT INTO item_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES (1, 22.90, 22.90, 2, 5);
INSERT INTO item_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES (1, 18.90, 18.90, 2, 4);
INSERT INTO item_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES (1, 45.90, 45.90, 3, 7);
INSERT INTO item_pedido (quantidade, preco_unitario, subtotal, pedido_id, produto_id) VALUES (1, 32.90, 32.90, 3, 8);
