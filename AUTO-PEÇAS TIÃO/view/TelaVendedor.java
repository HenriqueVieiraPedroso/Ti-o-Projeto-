package view;

import dao.ProdutoDAO;
import dao.PreVendaDAO;
import model.SessionManager;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TelaVendedor extends JFrame {

    private String[] colCatalogo = {"SKU", "Peça", "Montadora", "Preço Venda", "Estoque", "ID_INTERNO"};
    private DefaultTableModel mdlCatalogo = new DefaultTableModel(colCatalogo, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tblCatalogo = new JTable(mdlCatalogo);
    private TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(mdlCatalogo);

    private String[] colOrcamento = {"ID", "SKU", "Peça", "Montadora", "Preço Unit.", "Qtd", "Subtotal", "ID_INTERNO"};
    private DefaultTableModel mdlOrcamento = new DefaultTableModel(colOrcamento, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tblOrcamento = new JTable(mdlOrcamento);

    private JTextField txtBuscaSKU  = new JTextField();
    private JTextField txtQtd       = new JTextField();
    private JLabel     lblTotal      = new JLabel("Total do Orçamento: R$ 0,00");

    public TelaVendedor() {
        setTitle("Vendedor - Auto Peças Tião");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel pnlTopo = new JPanel(new BorderLayout());
        pnlTopo.setBackground(new Color(240, 240, 240));
        pnlTopo.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 20));

        JLabel lblTitulo = new JLabel("Orçamento / Pré-Venda");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(0, 120, 215));

        String nomeVendedor = SessionManager.getInstance().getUsuarioLogado() != null
            ? SessionManager.getInstance().getUsuarioLogado().getUsername() : "vendedor";
        JLabel lblUsuario = new JLabel("Olá, " + nomeVendedor + "  |  Perfil: Vendedor");
        lblUsuario.setFont(new Font("Monospaced", Font.PLAIN, 16));
        lblUsuario.setForeground(Color.GRAY);

        JButton btnSair = new JButton("Sair");
        btnSair.setBackground(new Color(200, 50, 50));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFont(new Font("Monospaced", Font.BOLD, 16));
        btnSair.setPreferredSize(new Dimension(120, 45));
        btnSair.setBorderPainted(false);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> { dispose(); new TelaLogin().setVisible(true); });

        JPanel pnlTopoDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTopoDir.setBackground(new Color(240, 240, 240));
        pnlTopoDir.add(lblUsuario);
        pnlTopoDir.add(btnSair);

        pnlTopo.add(lblTitulo, BorderLayout.WEST);
        pnlTopo.add(pnlTopoDir, BorderLayout.EAST);
        add(pnlTopo, BorderLayout.NORTH);

        JPanel pnlCard = new JPanel();
        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBackground(Color.WHITE);
        pnlCard.setPreferredSize(new Dimension(420, 0));
        pnlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        pnlCard.add(rotulo("Buscar por Nome ou SKU:"));
        pnlCard.add(Box.createVerticalStrut(10));
        txtBuscaSKU.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtBuscaSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtBuscaSKU.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(txtBuscaSKU);
        pnlCard.add(Box.createVerticalStrut(10));

        txtBuscaSKU.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filtrar() {
                String t = txtBuscaSKU.getText().trim();
                sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + t, 0, 1));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        JButton btnLimparBusca = criarBotao("Limpar Busca", new Color(150, 150, 150));
        btnLimparBusca.addActionListener(e -> { txtBuscaSKU.setText(""); sorter.setRowFilter(null); });
        pnlCard.add(btnLimparBusca);
        pnlCard.add(Box.createVerticalStrut(25));

        pnlCard.add(rotuloCinza("── Adicionar ao Orçamento ──"));
        pnlCard.add(Box.createVerticalStrut(15));
        pnlCard.add(rotulo("Quantidade:"));
        pnlCard.add(Box.createVerticalStrut(10));
        txtQtd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtQtd.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtQtd.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(txtQtd);
        pnlCard.add(Box.createVerticalStrut(15));

        JButton btnAdicionar = criarBotao("Adicionar Item Selecionado", new Color(0, 180, 80));
        JButton btnRemover   = criarBotao("Remover do Orçamento", new Color(200, 50, 50));
        JButton btnCancelar  = criarBotao("Cancelar Orçamento", Color.GRAY);

        pnlCard.add(btnAdicionar);
        pnlCard.add(Box.createVerticalStrut(10));
        pnlCard.add(btnRemover);
        pnlCard.add(Box.createVerticalStrut(10));
        pnlCard.add(btnCancelar);
        pnlCard.add(Box.createVerticalStrut(25));

        lblTotal.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTotal.setForeground(new Color(0, 130, 0));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCard.add(lblTotal);
        pnlCard.add(Box.createVerticalStrut(25));

        JButton btnEnviarCaixa = criarBotao("Enviar ao Caixa ✓", new Color(0, 100, 200));
        btnEnviarCaixa.setFont(new Font("Monospaced", Font.BOLD, 20));
        btnEnviarCaixa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        pnlCard.add(btnEnviarCaixa);

        add(pnlCard, BorderLayout.WEST);

        JPanel pnlCentral = new JPanel(new GridLayout(2, 1, 0, 15));
        pnlCentral.setBackground(new Color(240, 240, 240));
        pnlCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 15));

        formatarTabela(tblCatalogo);
        tblCatalogo.setRowSorter(sorter);
        tblCatalogo.getColumnModel().getColumn(5).setMinWidth(0);
        tblCatalogo.getColumnModel().getColumn(5).setMaxWidth(0);
        tblCatalogo.getColumnModel().getColumn(5).setWidth(0);

        formatarTabela(tblOrcamento);
        tblOrcamento.getColumnModel().getColumn(7).setMinWidth(0);
        tblOrcamento.getColumnModel().getColumn(7).setMaxWidth(0);
        tblOrcamento.getColumnModel().getColumn(7).setWidth(0);

        pnlCentral.add(painelTabela("Catálogo de Peças", tblCatalogo));
        pnlCentral.add(painelTabela("Orçamento Atual (Pré-Venda)", tblOrcamento));
        add(pnlCentral, BorderLayout.CENTER);

        btnAdicionar.addActionListener(e -> {
            int linha = tblCatalogo.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(this, "Selecione uma peça!"); return; }
            if (txtQtd.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Informe a quantidade!"); return; }
            try {
                int qtd = Integer.parseInt(txtQtd.getText().trim());
                if (qtd <= 0) throw new NumberFormatException();
                int modeloLinha = tblCatalogo.convertRowIndexToModel(linha);
                String sku = mdlCatalogo.getValueAt(modeloLinha, 0).toString();
                String peca = mdlCatalogo.getValueAt(modeloLinha, 1).toString();
                String mont = mdlCatalogo.getValueAt(modeloLinha, 2).toString();
                double preco = Double.parseDouble(mdlCatalogo.getValueAt(modeloLinha, 3).toString().replace("R$ ", "").replace(",", "."));
                int estoque = Integer.parseInt(mdlCatalogo.getValueAt(modeloLinha, 4).toString());
                String idProd = mdlCatalogo.getValueAt(modeloLinha, 5).toString();
                if (qtd > estoque) { JOptionPane.showMessageDialog(this, "Estoque insuficiente!"); return; }
                mdlOrcamento.addRow(new Object[]{"Novo", sku, peca, mont, String.format("R$ %.2f", preco), qtd, String.format("R$ %.2f", preco * qtd), idProd});
                txtQtd.setText(""); atualizarTotal();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Quantidade inválida!"); }
        });

        btnRemover.addActionListener(e -> {
            int linha = tblOrcamento.getSelectedRow();
            if (linha != -1) { mdlOrcamento.removeRow(linha); atualizarTotal(); }
        });

        btnCancelar.addActionListener(e -> {
            if (mdlOrcamento.getRowCount() > 0 && JOptionPane.showConfirmDialog(this, "Cancelar orçamento?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                mdlOrcamento.setRowCount(0); atualizarTotal();
            }
        });

        btnEnviarCaixa.addActionListener(e -> {
            if (mdlOrcamento.getRowCount() == 0) return;
            int idVendedor = SessionManager.getInstance().getUsuarioLogado().getIdUsuario();
            int idOrcamento = PreVendaDAO.gerarNovoIdOrcamento();
            boolean sucesso = true;
            for (int i = 0; i < mdlOrcamento.getRowCount(); i++) {
                if (PreVendaDAO.inserirPreVenda(idVendedor, mdlOrcamento.getValueAt(i, 7).toString(), Integer.parseInt(mdlOrcamento.getValueAt(i, 5).toString()), idOrcamento) == -1) {
                    sucesso = false; break;
                }
            }
            if (sucesso) { JOptionPane.showMessageDialog(this, "Orçamento #" + idOrcamento + " enviado!"); mdlOrcamento.setRowCount(0); atualizarTotal(); }
        });

        carregarCatalogo();
    }

    private void carregarCatalogo() {
        mdlCatalogo.setRowCount(0);
        List<Object[]> produtos = ProdutoDAO.listarProdutos();
        if (produtos != null) {
            for (Object[] p : produtos) {
                mdlCatalogo.addRow(new Object[]{p[0], p[1], p[2], String.format("R$ %.2f", (double) p[4]), p[5], p[6]});
            }
        }
    }

    private void atualizarTotal() {
        double total = 0;
        for (int i = 0; i < mdlOrcamento.getRowCount(); i++) {
            total += Double.parseDouble(mdlOrcamento.getValueAt(i, 6).toString().replace("R$ ", "").replace(",", "."));
        }
        lblTotal.setText(String.format("Total do Orçamento: R$ %.2f", total));
    }

    private void formatarTabela(JTable t) {
        t.setFont(new Font("Monospaced", Font.PLAIN, 18));
        t.setRowHeight(35);
        t.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 18));
        t.setSelectionBackground(new Color(180, 215, 255));
    }

    private JPanel painelTabela(String titulo, JTable tabela) {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel lbl = new JLabel(titulo); lbl.setFont(new Font("Monospaced", Font.BOLD, 18));
        lbl.setForeground(new Color(0, 120, 215));
        pnl.add(lbl, BorderLayout.NORTH); pnl.add(new JScrollPane(tabela), BorderLayout.CENTER); return pnl;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT); btn.setBackground(cor); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Monospaced", Font.BOLD, 16)); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); return btn;
    }

    private JLabel rotulo(String texto) {
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Monospaced", Font.BOLD, 18));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }

    private JLabel rotuloCinza(String texto) {
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Monospaced", Font.BOLD, 16));
        lbl.setForeground(new Color(100, 100, 100)); lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }

    public static void main(String[] args) { EventQueue.invokeLater(() -> new TelaVendedor().setVisible(true)); }
}
