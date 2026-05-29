package view;

import dao.PedidoCompraDAO;
import dao.ProdutoDAO;
import dao.VendaDAO;
import dao.UsuarioDAO;
import model.SessionManager;
import model.Usuario;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaGerente extends JFrame {

    private DefaultTableModel mdlUsers;

    public TelaGerente() {
        setTitle("Gerente - Auto Peças Tião");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblTitulo = new JLabel("Painel do Gerente");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(0, 120, 215));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Monospaced", Font.BOLD, 18));
        abas.setBackground(new Color(240, 240, 240));

        // 1. Aba Estoque
        String[] colEstoque = {"SKU", "Peça", "Montadora", "Custo", "Venda", "Lucro", "Estoque", "ID_INTERNO"};
        DefaultTableModel mdlEstoque = new DefaultTableModel(colEstoque, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        List<Object[]> produtos = ProdutoDAO.listarProdutos();
        if (produtos != null) {
            for (Object[] p : produtos) {
                double custo = (double) p[3];
                double venda = (double) p[4];
                double lucro = venda - custo;
                mdlEstoque.addRow(new Object[]{
                    p[0], p[1], p[2],
                    String.format("R$ %.2f", custo),
                    String.format("R$ %.2f", venda),
                    String.format("R$ %.2f", lucro),
                    p[5], p[6]
                });
            }
        }
        JTable tblEstoque = new JTable(mdlEstoque);
        estilizarTabela(tblEstoque);
        tblEstoque.getColumnModel().getColumn(7).setMinWidth(0);
        tblEstoque.getColumnModel().getColumn(7).setMaxWidth(0);
        tblEstoque.getColumnModel().getColumn(7).setWidth(0);
        
        abas.addTab("Estoque e Custos", new JScrollPane(tblEstoque));

        // 2. Aba Comissões
        String[] colComissao = {"Vendedor", "Vendas Realizadas", "Faturamento Total", "Comissão (1%)"};
        DefaultTableModel mdlComissao = new DefaultTableModel(colComissao, 0);

        List<Object[]> comissoes = VendaDAO.relatorioComissoes();
        if (comissoes != null) {
            for (Object[] c : comissoes) {
                mdlComissao.addRow(new Object[]{
                    c[0], c[1],
                    String.format("R$ %.2f", (double) c[2]),
                    String.format("R$ %.2f", (double) c[3])
                });
            }
        }
        JTable tblComissao = new JTable(mdlComissao);
        estilizarTabela(tblComissao);
        abas.addTab("Comissões", new JScrollPane(tblComissao));

        // 3. Aba Pedido de Compra
        JPanel pnlPedido = new JPanel(new BorderLayout());
        pnlPedido.setBackground(new Color(240, 240, 240));

        JPanel pnlPedidoConteudo = new JPanel(new BorderLayout(20, 20));
        pnlPedidoConteudo.setBackground(new Color(240, 240, 240));
        pnlPedidoConteudo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pnlCardPedido = criarCardBase(420);
        pnlCardPedido.add(criarTituloCard("Novo Pedido de Compra"));
        pnlCardPedido.add(Box.createVerticalStrut(20));

        pnlCardPedido.add(rotulo("Fornecedor:"));
        JTextField txtFornecedor = campoTexto();
        pnlCardPedido.add(txtFornecedor);
        pnlCardPedido.add(Box.createVerticalStrut(15));

        pnlCardPedido.add(rotulo("Peça:"));
        JTextField txtPeca = campoTexto();
        pnlCardPedido.add(txtPeca);
        pnlCardPedido.add(Box.createVerticalStrut(15));

        pnlCardPedido.add(rotulo("Quantidade:"));
        JTextField txtQtd = campoTexto();
        pnlCardPedido.add(txtQtd);
        pnlCardPedido.add(Box.createVerticalStrut(20));

        String[] colPedidos = {"ID", "Fornecedor", "Peça", "Quantidade", "Gerente", "Data"};
        DefaultTableModel mdlPedidos = new DefaultTableModel(colPedidos, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tblPedidos = new JTable(mdlPedidos);
        estilizarTabela(tblPedidos);

        JButton btnPedido = criarBotaoAcao("Enviar Pedido", new Color(0, 180, 80));
        JButton btnRemoverPedido = criarBotaoAcao("Remover Pedido", new Color(200, 50, 50));

        btnPedido.addActionListener(e -> {
            if (txtFornecedor.getText().isEmpty() || txtPeca.getText().isEmpty() || txtQtd.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                return;
            }
            try {
                int qtd = Integer.parseInt(txtQtd.getText().trim());
                Usuario userLogado = SessionManager.getInstance().getUsuarioLogado();
                int idGerente = (userLogado != null) ? userLogado.getIdUsuario() : -1;
                if (idGerente == -1) { JOptionPane.showMessageDialog(null, "Erro: Usuário não autenticado!"); return; }
                if (PedidoCompraDAO.inserirPedidoCompra(txtFornecedor.getText(), txtPeca.getText(), qtd, idGerente)) {
                    JOptionPane.showMessageDialog(null, "Pedido enviado com sucesso!");
                    txtFornecedor.setText(""); txtPeca.setText(""); txtQtd.setText("");
                    atualizarTabelaPedidos(mdlPedidos);
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Quantidade inválida!"); }
        });

        btnRemoverPedido.addActionListener(e -> {
            int linha = tblPedidos.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(null, "Selecione um pedido!"); return; }
            int idPedido = (int) mdlPedidos.getValueAt(linha, 0);
            if (PedidoCompraDAO.removerPedido(idPedido)) {
                JOptionPane.showMessageDialog(null, "Pedido removido!");
                atualizarTabelaPedidos(mdlPedidos);
            }
        });

        pnlCardPedido.add(btnPedido);
        pnlCardPedido.add(Box.createVerticalStrut(10));
        pnlCardPedido.add(btnRemoverPedido);

        pnlPedidoConteudo.add(pnlCardPedido, BorderLayout.WEST);
        pnlPedidoConteudo.add(new JScrollPane(tblPedidos), BorderLayout.CENTER);
        pnlPedido.add(pnlPedidoConteudo, BorderLayout.CENTER);
        abas.addTab("Pedido de Compra", pnlPedido);
        atualizarTabelaPedidos(mdlPedidos);

        // 4. Aba Gestão de Usuários
        JPanel pnlUsuarios = new JPanel(new BorderLayout());
        pnlUsuarios.setBackground(new Color(240, 240, 240));

        JPanel pnlUserConteudo = new JPanel(new BorderLayout(20, 20));
        pnlUserConteudo.setBackground(new Color(240, 240, 240));
        pnlUserConteudo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pnlCardUser = criarCardBase(420);
        pnlCardUser.add(criarTituloCard("Gerenciar Equipe"));
        pnlCardUser.add(Box.createVerticalStrut(20));

        pnlCardUser.add(rotulo("Usuário (Login):"));
        JTextField txtUsername = campoTexto();
        pnlCardUser.add(txtUsername);
        pnlCardUser.add(Box.createVerticalStrut(10));

        pnlCardUser.add(rotulo("Senha:"));
        JPasswordField txtSenha = new JPasswordField();
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtSenha.setFont(new Font("Monospaced", Font.PLAIN, 18));
        txtSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCardUser.add(txtSenha);
        pnlCardUser.add(Box.createVerticalStrut(10));

        pnlCardUser.add(rotulo("Perfil de Acesso:"));
        String[] perfis = {"VENDEDOR", "CAIXA", "ESTOQUISTA", "GERENTE", "AUTOATENDIMENTO"};
        JComboBox<String> cbPerfil = new JComboBox<>(perfis);
        cbPerfil.setFont(new Font("Monospaced", Font.PLAIN, 18));
        cbPerfil.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cbPerfil.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCardUser.add(cbPerfil);
        pnlCardUser.add(Box.createVerticalStrut(20));

        JButton btnAddUser = criarBotaoAcao("Adicionar Usuário", new Color(0, 120, 215));
        JButton btnRemUser = criarBotaoAcao("Remover Selecionado", new Color(200, 50, 50));

        String[] colUsers = {"ID", "Username", "Perfil"};
        mdlUsers = new DefaultTableModel(colUsers, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tblUsers = new JTable(mdlUsers);
        estilizarTabela(tblUsers);

        btnAddUser.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String pass = new String(txtSenha.getPassword());
            String perfil = cbPerfil.getSelectedItem().toString();
            if (user.isEmpty() || pass.isEmpty()) { JOptionPane.showMessageDialog(null, "Preencha tudo!"); return; }
            if (UsuarioDAO.inserirUsuario(user, pass, perfil)) {
                JOptionPane.showMessageDialog(null, "Usuário adicionado!");
                txtUsername.setText(""); txtSenha.setText("");
                atualizarTabelaUsuarios(mdlUsers);
            }
        });

        btnRemUser.addActionListener(e -> {
            int linha = tblUsers.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(null, "Selecione um usuário!"); return; }
            int idUser = (int) mdlUsers.getValueAt(linha, 0);
            if (JOptionPane.showConfirmDialog(null, "Remover?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (UsuarioDAO.removerUsuario(idUser)) {
                    JOptionPane.showMessageDialog(null, "Removido!");
                    atualizarTabelaUsuarios(mdlUsers);
                }
            }
        });

        pnlCardUser.add(btnAddUser);
        pnlCardUser.add(Box.createVerticalStrut(10));
        pnlCardUser.add(btnRemUser);

        pnlUserConteudo.add(pnlCardUser, BorderLayout.WEST);
        pnlUserConteudo.add(new JScrollPane(tblUsers), BorderLayout.CENTER);
        pnlUsuarios.add(pnlUserConteudo, BorderLayout.CENTER);
        abas.addTab("Gestão de Usuários", pnlUsuarios);
        atualizarTabelaUsuarios(mdlUsers);

        // 5. Aba Relatório Financeiro
        JPanel pnlRelatorio = new JPanel(new BorderLayout());
        pnlRelatorio.setBackground(new Color(240, 240, 240));
        JPanel pnlCardRel = criarCardBase(480);

        pnlCardRel.add(criarTituloCard("Resumo Financeiro"));
        pnlCardRel.add(Box.createVerticalStrut(20));

        double[] resumo = VendaDAO.resumoFinanceiro();
        if (resumo == null || resumo.length < 3) resumo = new double[]{0.0, 0.0, 0.0};

        JLabel lblFat  = rotuloValor("Faturamento Total:   R$ %.2f", resumo[0], new Color(0, 150, 0));
        JLabel lblLucro = rotuloValor("Lucro Total:         R$ %.2f", resumo[1], new Color(0, 150, 0));
        JLabel lblComTot = rotuloValor("Total em Comissões:  R$ %.2f", resumo[2], new Color(200, 100, 0));

        pnlCardRel.add(lblFat); pnlCardRel.add(Box.createVerticalStrut(10));
        pnlCardRel.add(lblLucro); pnlCardRel.add(Box.createVerticalStrut(10));
        pnlCardRel.add(lblComTot); pnlCardRel.add(Box.createVerticalStrut(20));

        JButton btnAtualizarRel = criarBotaoAcao("Atualizar Relatório", new Color(0, 120, 215));
        btnAtualizarRel.addActionListener(e -> {
            double[] r = VendaDAO.resumoFinanceiro();
            if (r == null || r.length < 3) r = new double[]{0.0, 0.0, 0.0};
            lblFat.setText(String.format("Faturamento Total:   R$ %.2f", r[0]));
            lblLucro.setText(String.format("Lucro Total:         R$ %.2f", r[1]));
            lblComTot.setText(String.format("Total em Comissões:  R$ %.2f", r[2]));
        });
        pnlCardRel.add(btnAtualizarRel);

        pnlRelatorio.add(pnlCardRel, BorderLayout.WEST);
        abas.addTab("Relatório Financeiro", pnlRelatorio);

        add(abas, BorderLayout.CENTER);
    }

    private void atualizarTabelaPedidos(DefaultTableModel mdl) {
        mdl.setRowCount(0);
        List<Object[]> pedidos = PedidoCompraDAO.listarPedidosPendentes();
        if (pedidos != null) for (Object[] p : pedidos) mdl.addRow(new Object[]{p[0], p[1], p[2], p[3], p[4], p[5]});
    }

    private void atualizarTabelaUsuarios(DefaultTableModel mdl) {
        mdl.setRowCount(0);
        List<Object[]> usuarios = UsuarioDAO.listarUsuarios();
        if (usuarios != null) for (Object[] u : usuarios) mdl.addRow(new Object[]{u[0], u[1], u[2]});
    }

    private void estilizarTabela(JTable tabela) {
        tabela.setFont(new Font("Monospaced", Font.PLAIN, 18));
        tabela.setRowHeight(35);
        tabela.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 18));
    }

    private JPanel criarCardBase(int largura) {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE);
        pnl.setPreferredSize(new Dimension(largura, 0));
        pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        return pnl;
    }

    private JLabel criarTituloCard(String texto) {
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Monospaced", Font.BOLD, 22));
        lbl.setForeground(new Color(0, 120, 215)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }

    private JButton criarBotaoAcao(String texto, Color corFundo) {
        JButton btn = new JButton(texto); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btn.setBackground(corFundo); btn.setForeground(Color.WHITE); btn.setFont(new Font("Monospaced", Font.BOLD, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); return btn;
    }

    private JLabel rotulo(String texto) {
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Monospaced", Font.BOLD, 18));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }

    private JTextField campoTexto() {
        JTextField txt = new JTextField(); txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txt.setAlignmentX(Component.LEFT_ALIGNMENT); txt.setFont(new Font("Monospaced", Font.PLAIN, 18)); return txt;
    }

    private JLabel rotuloValor(String formato, double valor, Color cor) {
        JLabel lbl = new JLabel(String.format(formato, valor)); lbl.setFont(new Font("Monospaced", Font.BOLD, 20));
        lbl.setForeground(cor); lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }

    public static void main(String[] args) { EventQueue.invokeLater(() -> new TelaGerente().setVisible(true)); }
}
