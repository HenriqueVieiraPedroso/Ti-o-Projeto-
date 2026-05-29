package view;

import dao.ProdutoDAO;
import dao.PreVendaDAO;
import dao.VendaDAO;
import model.SessionManager;
import model.Usuario;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaAtendimento extends JFrame {

    private DefaultTableModel modeloPecas;
    private DefaultTableModel modeloCarrinho;
    private JTable tabelaPecas;
    private JTable tabelaCarrinho;
    private JLabel lblTotal;
    private JComboBox<String> cmbPagto;
    private List<Object[]> listaProdutos;

    public TelaAtendimento() {
        setTitle("Autoatendimento - Auto Peças Tião");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(240, 240, 240));

        Font fTitulo = new Font("Monospaced", Font.BOLD, 36);
        Font fLabel = new Font("Monospaced", Font.BOLD, 20);
        Font fTexto = new Font("Monospaced", Font.PLAIN, 22);
        Font fBotao = new Font("Monospaced", Font.BOLD, 24);
        Font fTabela = new Font("Monospaced", Font.PLAIN, 20);
        Font fHeader = new Font("Monospaced", Font.BOLD, 22);

        JPanel pnlTopo = new JPanel(new BorderLayout());
        pnlTopo.setBackground(new Color(0, 120, 215));
        JLabel lblTitulo = new JLabel(" AUTOATENDIMENTO - COMPRE SOZINHO ", JLabel.CENTER);
        lblTitulo.setFont(fTitulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        pnlTopo.add(lblTitulo, BorderLayout.CENTER);
        add(pnlTopo, BorderLayout.NORTH);

        JPanel pnlPrincipal = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlPrincipal.setBackground(new Color(240, 240, 240));
        pnlPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel pnlEsq = new JPanel(new BorderLayout(10, 10));
        pnlEsq.setBackground(Color.WHITE);
        pnlEsq.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "1. ESCOLHA AS PEÇAS", 0, 0, fHeader));

        String[] colPecas = {"SKU", "Peça", "Montadora", "Preço", "Estoque", "ID"};
        modeloPecas = new DefaultTableModel(colPecas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaPecas = new JTable(modeloPecas);
        formatarTabela(tabelaPecas, fTabela, fHeader);
        sumirColuna(tabelaPecas, 5);
        
        JScrollPane scrollPecas = new JScrollPane(tabelaPecas);
        pnlEsq.add(scrollPecas, BorderLayout.CENTER);

        JPanel pnlBusca = new JPanel(new BorderLayout(10, 10));
        pnlBusca.setBackground(Color.WHITE);
        JTextField txtBusca = new JTextField();
        txtBusca.setFont(fTexto);
        JButton btnBuscar = new JButton("BUSCAR");
        btnBuscar.setFont(fBotao);
        btnBuscar.setBackground(new Color(0, 120, 215));
        btnBuscar.setForeground(Color.WHITE);
        
        pnlBusca.add(new JLabel(" Pesquisar: ", JLabel.RIGHT) {{ setFont(fLabel); }}, BorderLayout.WEST);
        pnlBusca.add(txtBusca, BorderLayout.CENTER);
        pnlBusca.add(btnBuscar, BorderLayout.EAST);
        pnlEsq.add(pnlBusca, BorderLayout.NORTH);

        JButton btnAdd = new JButton("ADICIONAR AO CARRINHO");
        btnAdd.setFont(fBotao);
        btnAdd.setBackground(new Color(0, 150, 0));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(0, 70));
        pnlEsq.add(btnAdd, BorderLayout.SOUTH);

        JPanel pnlDir = new JPanel(new BorderLayout(10, 10));
        pnlDir.setBackground(Color.WHITE);
        pnlDir.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "2. SEU CARRINHO", 0, 0, fHeader));

        String[] colCarrinho = {"Peça", "Preço Un.", "Qtd", "Subtotal", "ID"};
        modeloCarrinho = new DefaultTableModel(colCarrinho, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaCarrinho = new JTable(modeloCarrinho);
        formatarTabela(tabelaCarrinho, fTabela, fHeader);
        sumirColuna(tabelaCarrinho, 4);

        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        pnlDir.add(scrollCarrinho, BorderLayout.CENTER);

        JPanel pnlFim = new JPanel(new GridLayout(2, 1));
        pnlFim.setBackground(new Color(230, 230, 230));

        JPanel pnlPagto = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlPagto.setBackground(new Color(230, 230, 230));
        JLabel lblPag = new JLabel("FORMA DE PAGAMENTO: ");
        lblPag.setFont(fLabel);
        cmbPagto = new JComboBox<>(new String[]{"DINHEIRO", "PIX", "CARTÃO", "CHEQUE"});
        cmbPagto.setFont(fTexto);
        pnlPagto.add(lblPag);
        pnlPagto.add(cmbPagto);

        JPanel pnlTotal = new JPanel(new BorderLayout());
        pnlTotal.setBackground(new Color(230, 230, 230));
        lblTotal = new JLabel(" TOTAL: R$ 0,00  ", JLabel.RIGHT);
        lblTotal.setFont(new Font("Monospaced", Font.BOLD, 30));
        
        JButton btnDel = new JButton("REMOVER ITEM");
        btnDel.setFont(fBotao);
        btnDel.setBackground(Color.RED);
        btnDel.setForeground(Color.WHITE);
        
        pnlTotal.add(btnDel, BorderLayout.WEST);
        pnlTotal.add(lblTotal, BorderLayout.CENTER);

        pnlFim.add(pnlPagto);
        pnlFim.add(pnlTotal);
        
        pnlDir.add(pnlFim, BorderLayout.NORTH);

        JButton btnVender = new JButton("FINALIZAR COMPRA AGORA");
        btnVender.setFont(fBotao);
        btnVender.setBackground(new Color(0, 120, 215));
        btnVender.setForeground(Color.WHITE);
        btnVender.setPreferredSize(new Dimension(0, 70));
        pnlDir.add(btnVender, BorderLayout.SOUTH);

        pnlPrincipal.add(pnlEsq);
        pnlPrincipal.add(pnlDir);
        add(pnlPrincipal, BorderLayout.CENTER);

        atualizarCatalogo();

        btnBuscar.addActionListener(e -> filtrar(txtBusca.getText()));
        
        btnAdd.addActionListener(e -> {
            int row = tabelaPecas.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um produto primeiro!");
                return;
            }
            
            String nome = modeloPecas.getValueAt(row, 1).toString();
            double valor = Double.parseDouble(modeloPecas.getValueAt(row, 3).toString().replace("R$ ", "").replace(",", "."));
            int qtdEstoque = (int) modeloPecas.getValueAt(row, 4);
            String idProd = modeloPecas.getValueAt(row, 5).toString();

            if (qtdEstoque <= 0) {
                JOptionPane.showMessageDialog(this, "Sem estoque!");
                return;
            }

            for (int i = 0; i < modeloCarrinho.getRowCount(); i++) {
                if (modeloCarrinho.getValueAt(i, 4).toString().equals(idProd)) {
                    int atual = (int) modeloCarrinho.getValueAt(i, 2);
                    if (atual + 1 > qtdEstoque) {
                        JOptionPane.showMessageDialog(this, "Estoque máximo atingido!");
                        return;
                    }
                    modeloCarrinho.setValueAt(atual + 1, i, 2);
                    modeloCarrinho.setValueAt(String.format("R$ %.2f", valor * (atual + 1)), i, 3);
                    calcularTotal();
                    return;
                }
            }

            modeloCarrinho.addRow(new Object[]{nome, String.format("R$ %.2f", valor), 1, String.format("R$ %.2f", valor), idProd});
            calcularTotal();
        });

        btnDel.addActionListener(e -> {
            int row = tabelaCarrinho.getSelectedRow();
            if (row == -1) return;
            modeloCarrinho.removeRow(row);
            calcularTotal();
        });

        btnVender.addActionListener(e -> {
            if (modeloCarrinho.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Carrinho vazio!");
                return;
            }

            int ok = JOptionPane.showConfirmDialog(this, "Finalizar compra no " + cmbPagto.getSelectedItem() + "?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            gravarVenda();
        });

        cmbPagto.addActionListener(e -> calcularTotal());
    }

    private void formatarTabela(JTable t, Font f, Font h) {
        t.setFont(f);
        t.setRowHeight(50);
        t.getTableHeader().setFont(h);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void sumirColuna(JTable t, int c) {
        t.getColumnModel().getColumn(c).setMinWidth(0);
        t.getColumnModel().getColumn(c).setMaxWidth(0);
        t.getColumnModel().getColumn(c).setWidth(0);
    }

    private void atualizarCatalogo() {
        listaProdutos = ProdutoDAO.listarProdutos();
        modeloPecas.setRowCount(0);
        if (listaProdutos != null) {
            for (Object[] p : listaProdutos) {
                modeloPecas.addRow(new Object[]{
                    p[0], p[1], p[2], 
                    String.format("R$ %.2f", (double) p[4]), 
                    p[5], p[6]
                });
            }
        }
    }

    private void filtrar(String txt) {
        modeloPecas.setRowCount(0);
        String busca = txt.toLowerCase();
        if (listaProdutos != null) {
            for (Object[] p : listaProdutos) {
                if (p[0].toString().toLowerCase().contains(busca) || p[1].toString().toLowerCase().contains(busca)) {
                    modeloPecas.addRow(new Object[]{
                        p[0], p[1], p[2], 
                        String.format("R$ %.2f", (double) p[4]), 
                        p[5], p[6]
                    });
                }
            }
        }
    }

    private void calcularTotal() {
        double total = 0;
        for (int i = 0; i < modeloCarrinho.getRowCount(); i++) {
            total += Double.parseDouble(modeloCarrinho.getValueAt(i, 3).toString().replace("R$ ", "").replace(",", "."));
        }
        String pagto = cmbPagto.getSelectedItem().toString();
        if (pagto.equals("DINHEIRO") || pagto.equals("PIX")) {
            total *= 0.9;
            lblTotal.setText(String.format(" TOTAL: R$ %.2f (10%% OFF) ", total));
        } else {
            lblTotal.setText(String.format(" TOTAL: R$ %.2f  ", total));
        }
    }

    private void gravarVenda() {
        Usuario u = SessionManager.getInstance().getUsuarioLogado();
        int idVendedor = (u != null) ? u.getIdUsuario() : 7;
        String pagto = cmbPagto.getSelectedItem().toString();

        try {
            int idOrc = PreVendaDAO.gerarNovoIdOrcamento();
            boolean ok = true;

            for (int i = 0; i < modeloCarrinho.getRowCount(); i++) {
                String idProd = modeloCarrinho.getValueAt(i, 4).toString();
                int qtd = (int) modeloCarrinho.getValueAt(i, 2);
                double preco = Double.parseDouble(modeloCarrinho.getValueAt(i, 1).toString().replace("R$ ", "").replace(",", "."));
                
                int idPV = PreVendaDAO.inserirPreVenda(idVendedor, idProd, qtd, idOrc);
                
                if (idPV != -1) {
                    double desc = (pagto.equals("DINHEIRO") || pagto.equals("PIX")) ? 0.9 : 1.0;
                    double vFinal = (preco * qtd) * desc; 
                    double comis = (preco * qtd) * 0.01;
                    
                    if (!VendaDAO.finalizarVenda(idPV, pagto, preco * qtd, vFinal, comis, idProd, qtd)) {
                        ok = false;
                    }
                } else {
                    ok = false;
                }
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, "Venda realizada com sucesso!");
                modeloCarrinho.setRowCount(0);
                calcularTotal();
                atualizarCatalogo();
            } else {
                JOptionPane.showMessageDialog(this, "Erro em alguns itens.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new TelaAtendimento().setVisible(true);
        });
    }
}
