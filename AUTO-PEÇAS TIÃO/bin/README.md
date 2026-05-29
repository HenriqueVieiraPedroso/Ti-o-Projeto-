# Auto Peças Tião — Integração com Banco de Dados
## Arquivos desta entrega

| Arquivo           | Descrição                                      |
|-------------------|------------------------------------------------|
| ConexaoDB.java    | Configuração da conexão MySQL                  |
| DAO.java          | Todas as queries (login, produtos, vendas...)  |
| TelaLogin.java    | Login real com redirecionamento por perfil     |
| TelaEstoquista.java | CRUD de produtos com banco                   |
| TelaVendedor.java | Catálogo do banco + pré-vendas persistidas     |
| TelaCaixa.java    | Busca pré-vendas pendentes + finaliza vendas   |
| TelaGerente.java  | Relatórios financeiros e comissões do banco    |
| TelaAtendimento.java | Consulta somente-leitura do banco           |

---

## PASSO 1 — Configurar a conexão

Abra **ConexaoDB.java** e ajuste as 4 linhas:

```java
private static final String HOST    = "localhost";   // IP da VM ou "localhost"
private static final int    PORTA   = 3306;
private static final String USUARIO = "root";        // seu usuário MySQL
private static final String SENHA   = "";            // sua senha MySQL
```

### Se o MySQL está na VM do curso:
1. Ligue a VM
2. No terminal da VM, descubra o IP: `ip addr` ou `hostname -I`
3. Coloque esse IP no campo HOST, ex: `"192.168.56.101"`
4. Verifique se o MySQL da VM aceita conexões externas:
   - No MySQL da VM: `GRANT ALL ON autopecas_tiao.* TO 'root'@'%';`
   - No arquivo `/etc/mysql/mysql.conf.d/mysqld.cnf`, mude:
     `bind-address = 127.0.0.1`  para  `bind-address = 0.0.0.0`
   - Reinicie o MySQL: `sudo systemctl restart mysql`

### Se instalou MySQL local:
- HOST = "localhost", configure USUARIO e SENHA normalmente.

---

## PASSO 2 — Baixar o driver JDBC

O Java precisa de um JAR para conectar ao MySQL:

1. Baixe em: https://dev.mysql.com/downloads/connector/j/
   (escolha "Platform Independent" > .zip)
2. Extraia e copie o arquivo `mysql-connector-j-X.X.X.jar`
   para a mesma pasta dos seus .java

---

## PASSO 3 — Compilar e rodar

```bash
# Na pasta com todos os .java e o .jar:
javac -cp .;mysql-connector-j-*.jar *.java        # Windows
javac -cp .:mysql-connector-j-*.jar *.java        # Linux/Mac

# Rodar:
java -cp .;mysql-connector-j-*.jar TelaLogin       # Windows
java -cp .:mysql-connector-j-*.jar TelaLogin       # Linux/Mac
```

---

## Usuários de teste (já no banco)

| Usuário   | Senha           | Perfil         |
|-----------|-----------------|----------------|
| ADM       | admin           | Gerente        |
| vendedor1 | vendas123       | Vendedor       |
| vendedor2 | vendas123       | Vendedor       |
| vendedor3 | vendas123       | Vendedor       |
| caixa     | caixa123        | Caixa          |
| estoque   | estoque123      | Estoquista     |
| terminal  | autoatendimento | Autoatendimento|

---

## Fluxo completo do sistema

1. **Estoquista** entra no estoque e cadastra produtos (custo → preço venda = custo × 1,4)
2. **Vendedor** consulta o catálogo e adiciona itens ao orçamento (vira pré-venda no banco)
3. **Caixa** carrega as pré-vendas pendentes, aplica forma de pagamento
   - PIX ou Dinheiro → 10% de desconto automático
   - Finaliza → estoque é atualizado + comissão de 1% registrada
4. **Gerente** vê relatório com faturamento, lucro e comissões por vendedor
5. **Terminal** consulta preços sem acesso a dados internos (sem custo, sem lucro)
