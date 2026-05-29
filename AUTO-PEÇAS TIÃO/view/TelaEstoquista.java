package view;

import dao.ProdutoDAO;
import dao.MontadoraDAO;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class TelaEstoquista extends JFrame {

    private JTextField txtSKU       = new JTextField();
    private JTextField txtPeca      = new JTextField();
    private JComboBox<String> cmbMarca = new JComboBox<>();
    private JTextField txtValorCusto = new JTextField();
    private JTextField txtQuantidade = new JTextField();
    private JLabel lblPreco          = new JLabel("Preço de venda: R$ 0,00");
    private JButton btnCadastro      = new JButton("Cadastrar");
    private JButton btnEditar        = new JButton("Editar");
    private JButton btnRemover       = new JButton("Remover");

    private String[] colunas = {"SKU", "Peça", "Montadora", "Custo", "Preço Venda", "Quantidade", "ID_INTERNO"};
    private DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JTable tabela = new JTable(modelo);
    private JLabel lblBarra      = new JLabel("Pesquisa: ");
    private JTextField txtPesquisa = new JTextField();
    private TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modelo);

    public TelaEstoquista() {
        setTitle("Estoquista - Auto Peças Tião");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(240, 240, 240));

        JLabel lblTitulo = new JLabel("Entrada de Produto");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(0, 120, 215));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlCard = new JPanel();
        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBackground(Color.WHITE);
        pnlCard.setPreferredSize(new Dimension(420, 0));
        pnlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        lblBarra.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblBarra.setForeground(Color.BLACK);
        lblBarra.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPesquisa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtPesquisa.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPesquisa.setFont(new Font("Monospaced", Font.PLAIN, 18));

        txtPesquisa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filtrar() {
                String texto = txtPesquisa.getText().trim();
                sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + texto, 0));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        pnlCard.add(lblBarra);
        pnlCard.add(Box.createVerticalStrut(10));
        pnlCard.add(txtPesquisa);
        pnlCard.add(Box.createVerticalStrut(20));

        pnlCard.add(rotulo("SKU:"));
        pnlCard.add(Box.createVerticalStrut(10));
        txtSKU.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtSKU.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSKU.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(txtSKU);
        pnlCard.add(Box.createVerticalStrut(15));

        pnlCard.add(rotulo("Peça:"));
        pnlCard.add(Box.createVerticalStrut(10));
        txtPeca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtPeca.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPeca.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(txtPeca);
        pnlCard.add(Box.createVerticalStrut(15));

        pnlCard.add(rotulo("Valor Custo:"));
        pnlCard.add(Box.createVerticalStrut(10));
        txtValorCusto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtValorCusto.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtValorCusto.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(txtValorCusto);
        pnlCard.add(Box.createVerticalStrut(15));

        pnlCard.add(rotulo("Quantidade:"));
        pnlCard.add(Box.createVerticalStrut(10));
        txtQuantidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtQuantidade.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtQuantidade.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(txtQuantidade);
        pnlCard.add(Box.createVerticalStrut(15));

        pnlCard.add(rotulo("Marca:"));
        pnlCard.add(Box.createVerticalStrut(10));
        cmbMarca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cmbMarca.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbMarca.setFont(new Font("Monospaced", Font.PLAIN, 18));
        pnlCard.add(cmbMarca);
        pnlCard.add(Box.createVerticalStrut(20));

        lblPreco.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblPreco.setForeground(new Color(0, 150, 0));
        lblPreco.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCard.add(lblPreco);
        pnlCard.add(Box.createVerticalStrut(10));

        btnCadastro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        btnCadastro.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastro.setBackground(new Color(0, 180, 80));
        btnCadastro.setForeground(Color.WHITE);
        btnCadastro.setFont(new Font("Monospaced", Font.BOLD, 20));
        btnCadastro.setFocusPainted(false);
        btnCadastro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlCard.add(btnCadastro);
        pnlCard.add(Box.createVerticalStrut(10));

        JPanel pnlBotoes = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlBotoes.setBackground(Color.WHITE);
        pnlBotoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlBotoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        btnEditar.setBackground(Color.GRAY);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFont(new Font("Monospaced", Font.BOLD, 18));
        btnEditar.setBorderPainted(false);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnRemover.setBackground(Color.RED);
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setFont(new Font("Monospaced", Font.BOLD, 18));
        btnRemover.setBorderPainted(false);
        btnRemover.setFocusPainted(false);
        btnRemover.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlBotoes.add(btnEditar);
        pnlBotoes.add(btnRemover);
        pnlCard.add(pnlBotoes);

        add(pnlCard, BorderLayout.WEST);

        txtValorCusto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void calcular() {
                try {
                    double custo = Double.parseDouble(txtValorCusto.getText().trim());
                    lblPreco.setText(String.format("Preço de venda: R$ %.2f", custo * 1.4));
                } catch (NumberFormatException ex) {
                    lblPreco.setText("Preço de venda: R$ 0,00");
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { calcular(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { calcular(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcular(); }
        });

        btnCadastro.addActionListener(e -> {
            if (txtSKU.getText().isEmpty() || txtPeca.getText().isEmpty() ||
                    txtValorCusto.getText().isEmpty() || txtQuantidade.getText().isEmpty()
                    || cmbMarca.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }
            try {
                String sku    = txtSKU.getText().trim();
                String peca   = txtPeca.getText().trim();
                double custo  = Double.parseDouble(txtValorCusto.getText().trim());
                int    qtd    = Integer.parseInt(txtQuantidade.getText().trim());
                String mont   = cmbMarca.getSelectedItem().toString();

                int idMontadora = -1;
                List<Object[]> montadoras = MontadoraDAO.listarMontadoras();
                if (montadoras != null) {
                    for (Object[] m : montadoras) {
                        if (m[1].toString().equals(mont)) { idMontadora = (int) m[0]; break; }
                    }
                }
                
                String novoId = ProdutoDAO.gerarNovoId();
                if (ProdutoDAO.inserirProduto(novoId, idMontadora, peca, sku, "", custo, qtd)) {
                    recarregarTabela();
                    limparCampos();
                    JOptionPane.showMessageDialog(this, "Produto " + novoId + " cadastrado!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro nos valores!");
            }
        });

        btnRemover.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) return;
            int ml = tabela.convertRowIndexToModel(linha);
            String idInterno = modelo.getValueAt(ml, 6).toString();
            if (JOptionPane.showConfirmDialog(this, "Remover?", "Confirma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (ProdutoDAO.removerProduto(idInterno)) {
                    recarregarTabela();
                    JOptionPane.showMessageDialog(this, "Removido!");
                }
            }
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) return;
            try {
                int ml = tabela.convertRowIndexToModel(linha);
                String idInterno = modelo.getValueAt(ml, 6).toString();
                String sku   = txtSKU.getText().trim();
                double custo = Double.parseDouble(txtValorCusto.getText().trim());
                int    qtd   = Integer.parseInt(txtQuantidade.getText().trim());
                String mont  = cmbMarca.getSelectedItem().toString();
                int idM = -1;
                List<Object[]> mnts = MontadoraDAO.listarMontadoras();
                if (mnts != null) for (Object[] m : mnts) if (m[1].toString().equals(mont)) idM = (int) m[0];

                if (ProdutoDAO.atualizarProduto(idInterno, idM, txtPeca.getText().trim(), sku, "", custo, qtd)) {
                    recarregarTabela();
                    limparCampos();
                    JOptionPane.showMessageDialog(this, "Atualizado!");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro!"); }
        });

        tabela.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha == -1) return;
                int ml = tabela.convertRowIndexToModel(linha);
                txtSKU.setText(modelo.getValueAt(ml, 0).toString());
                txtPeca.setText(modelo.getValueAt(ml, 1).toString());
                txtValorCusto.setText(modelo.getValueAt(ml, 3).toString().replace("R$ ", "").replace(",", "."));
                txtQuantidade.setText(modelo.getValueAt(ml, 5).toString());
                String mont = modelo.getValueAt(ml, 2).toString();
                for (int i = 0; i < cmbMarca.getItemCount(); i++) if (cmbMarca.getItemAt(i).equals(mont)) cmbMarca.setSelectedIndex(i);
            }
        });

        tabela.setFont(new Font("Monospaced", Font.PLAIN, 18));
        tabela.setRowHeight(35);
        tabela.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 18));
        tabela.setRowSorter(sorter);
        tabela.getColumnModel().getColumn(6).setMinWidth(0);
        tabela.getColumnModel().getColumn(6).setMaxWidth(0);
        tabela.getColumnModel().getColumn(6).setWidth(0);
        
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        cmbMarca.addItem("Selecione...");
        List<Object[]> ms = MontadoraDAO.listarMontadoras();
        if (ms != null) for (Object[] m : ms) cmbMarca.addItem(m[1].toString());

        recarregarTabela();
    }

    private void recarregarTabela() {
        modelo.setRowCount(0);
        List<Object[]> prods = ProdutoDAO.listarProdutos();
        if (prods != null) {
            for (Object[] p : prods) {
                modelo.addRow(new Object[]{p[0], p[1], p[2], String.format("R$ %.2f", (double) p[3]), String.format("R$ %.2f", (double) p[4]), p[5], p[6]});
            }
        }
    }

    private void limparCampos() {
        txtSKU.setText(""); txtPeca.setText(""); txtValorCusto.setText(""); txtQuantidade.setText(""); cmbMarca.setSelectedIndex(0); lblPreco.setText("Preço de venda: R$ 0,00");
    }

    private JLabel rotulo(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Monospaced", Font.BOLD, 18));
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> { new TelaEstoquista().setVisible(true); });
    }
}
