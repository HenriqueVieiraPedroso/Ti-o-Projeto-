package view;

import dao.PreVendaDAO;
import dao.VendaDAO;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaCaixa extends JFrame {

    private String[]          colPendentes = {"Orçamento", "Vendedor", "Qtd Itens", "Subtotal"};
    private DefaultTableModel mdlPendentes = new DefaultTableModel(colPendentes, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tblPendentes = new JTable(mdlPendentes);
    
    private String[]          colHistorico = {"ID", "Vendedor", "Peça", "Subtotal", "Desconto", "Total Final", "Pagamento", "Comissão"};
    private DefaultTableModel mdlHistorico = new DefaultTableModel(colHistorico, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private JTable tblHistorico = new JTable(mdlHistorico);

    private JComboBox<String> cmbPagamento = new JComboBox<>(new String[]{
        "Cartão de Crédito", "Cartão de Débito", "PIX", "Dinheiro", "Cheque"});

    private JLabel lblSubtotal       = new JLabel("Subtotal:          R$ 0,00");
    private JLabel lblDesconto       = new JLabel("Desconto:          R$ 0,00");
    private JLabel lblTotal          = new JLabel("TOTAL:             R$ 0,00");
    private JLabel lblComissao       = new JLabel("Comissão (1%):     R$ 0,00");
    private JLabel lblFaturamentoDia = new JLabel("Faturamento do dia: R$ 0,00");

    private JButton btnFinalizar  = new JButton("Finalizar Venda");
    private JButton btnAtualizar  = new JButton("Atualizar Lista");
    private JButton btnSair       = new JButton("Sair");

    private double faturamentoDia = 0;

    public TelaCaixa(String nomeUsuario) {
        setTitle("Caixa - Auto Peças Tião");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel pnlTopo = new JPanel(new BorderLayout());
        pnlTopo.setBackground(new Color(240, 240, 240));
        pnlTopo.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 20));

        JLabel lblTitulo = new JLabel("Finalização de Venda");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(0, 120, 215));

        JPanel pnlTopoDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTopoDir.setBackground(new Color(240, 240, 240));
        JLabel lblUsuario = new JLabel("Olá, " + nomeUsuario + "  |  Perfil: Caixa");
        lblUsuario.setFont(new Font("Monospaced", Font.PLAIN, 16));
        lblUsuario.setForeground(Color.GRAY);

        btnSair.setBackground(new Color(200, 50, 50));
        btnSair.setForeground(Color.WHITE);
        btnSair.setFont(new Font("Monospaced", Font.BOLD, 16));
        btnSair.setPreferredSize(new Dimension(120, 45));
        btnSair.setBorderPainted(false);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> { dispose(); new TelaLogin().setVisible(true); });

        pnlTopoDir.add(lblUsuario);
        pnlTopoDir.add(btnSair);
        pnlTopo.add(lblTitulo, BorderLayout.WEST);
        pnlTopo.add(pnlTopoDir, BorderLayout.EAST);
        add(pnlTopo, BorderLayout.NORTH);

        JPanel pnlCard = new JPanel();
        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBackground(Color.WHITE);
        pnlCard.setPreferredSize(new Dimension(450, 0));
        pnlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblCard = new JLabel("Processar Pagamento");
        lblCard.setFont(new Font("Monospaced", Font.BOLD, 22));
        lblCard.setForeground(new Color(0, 120, 215));
        lblCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCard.add(lblCard);
        pnlCard.add(Box.createVerticalStrut(20));

        pnlCard.add(rotulo("Selecione a pré-venda na tabela,"));
        pnlCard.add(rotulo("escolha o pagamento e finalize."));
        pnlCard.add(Box.createVerticalStrut(25));

        pnlCard.add(rotulo("Forma de Pagamento:"));
        pnlCard.add(Box.createVerticalStrut(10));
        cmbPagamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        cmbPagamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbPagamento.setFont(new Font("Monospaced", Font.PLAIN, 18));
        cmbPagamento.addActionListener(e -> recalcularPreview());
        pnlCard.add(cmbPagamento);
        pnlCard.add(Box.createVerticalStrut(25));

        estilizarLabel(lblSubtotal, 18, false, Color.DARK_GRAY);
        estilizarLabel(lblDesconto, 18, false, new Color(180, 80, 0));
        estilizarLabel(lblTotal, 22, true, new Color(0, 130, 0));
        estilizarLabel(lblComissao, 16, false, Color.GRAY);
        estilizarLabel(lblFaturamentoDia, 16, true, new Color(0, 100, 0));

        pnlCard.add(lblSubtotal);
        pnlCard.add(Box.createVerticalStrut(8));
        pnlCard.add(lblDesconto);
        pnlCard.add(Box.createVerticalStrut(8));
        pnlCard.add(lblTotal);
        pnlCard.add(Box.createVerticalStrut(8));
        pnlCard.add(lblComissao);
        pnlCard.add(Box.createVerticalStrut(30));

        btnFinalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        btnFinalizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnFinalizar.setBackground(new Color(0, 100, 200));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setFont(new Font("Monospaced", Font.BOLD, 18));
        btnFinalizar.setFocusPainted(false);
        btnFinalizar.setBorderPainted(false);
        btnFinalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlCard.add(btnFinalizar);
        pnlCard.add(Box.createVerticalStrut(12));

        btnAtualizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btnAtualizar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAtualizar.setBackground(new Color(100, 100, 100));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFont(new Font("Monospaced", Font.BOLD, 16));
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorderPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(e -> carregarPendentes());
        pnlCard.add(btnAtualizar);
        pnlCard.add(Box.createVerticalGlue());
        pnlCard.add(lblFaturamentoDia);

        add(pnlCard, BorderLayout.WEST);

        JPanel pnlCentral = new JPanel(new GridLayout(2, 1, 0, 15));
        pnlCentral.setBackground(new Color(240, 240, 240));
        pnlCentral.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 15));
        pnlCentral.add(painelTabela("Pré-Vendas Pendentes", tblPendentes));
        pnlCentral.add(painelTabela("Vendas Finalizadas nesta Sessão", tblHistorico));
        tblHistorico.setEnabled(false);
        add(pnlCentral, BorderLayout.CENTER);

        JPanel pnlRodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlRodape.setBackground(new Color(220, 220, 220));
        JLabel lblRodape = new JLabel("Auto Peças do Tião  |  Módulo: Caixa");
        lblRodape.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lblRodape.setForeground(Color.GRAY);
        pnlRodape.add(lblRodape);
        add(pnlRodape, BorderLayout.SOUTH);

        tblPendentes.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) recalcularPreview();
        });

        btnFinalizar.addActionListener(e -> finalizarVenda());
        carregarPendentes();
    }

    public TelaCaixa() { this("caixa"); }

    private void carregarPendentes() {
        mdlPendentes.setRowCount(0);
        List<Object[]> lista = PreVendaDAO.listarPreVendasPendentes();
        java.util.Map<Integer, Double> totais = new java.util.HashMap<>();
        java.util.Map<Integer, Integer> contagem = new java.util.HashMap<>();
        java.util.Map<Integer, String> vendedores = new java.util.HashMap<>();
        if (lista != null) {
            for (Object[] pv : lista) {
                Integer idOrc = (Integer) pv[8];
                if (idOrc == null) idOrc = 0;
                double sub = (double) pv[3] * (int) pv[4];
                totais.put(idOrc, totais.getOrDefault(idOrc, 0.0) + sub);
                contagem.put(idOrc, contagem.getOrDefault(idOrc, 0) + 1);
                vendedores.put(idOrc, (String) pv[1]);
            }
        }
        for (Integer idOrc : totais.keySet()) {
            mdlPendentes.addRow(new Object[]{ idOrc == 0 ? "Individual" : idOrc, vendedores.get(idOrc), contagem.get(idOrc), String.format("R$ %.2f", totais.get(idOrc)) });
        }
        recalcularPreview();
    }

    private void recalcularPreview() {
        int linha = tblPendentes.getSelectedRow();
        if (linha == -1) {
            lblSubtotal.setText("Subtotal:          R$ 0,00"); lblDesconto.setText("Desconto:          R$ 0,00");
            lblTotal.setText("TOTAL:             R$ 0,00"); lblComissao.setText("Comissão (1%):     R$ 0,00"); return;
        }
        double subtotal = Double.parseDouble(mdlPendentes.getValueAt(linha, 3).toString().replace("R$ ", "").replace(",", "."));
        String pgto = cmbPagamento.getSelectedItem().toString();
        boolean temDesconto = pgto.equals("PIX") || pgto.equals("Dinheiro");
        double desconto = temDesconto ? subtotal * 0.10 : 0;
        double total = subtotal - desconto;
        lblSubtotal.setText(String.format("Subtotal:          R$ %.2f", subtotal));
        lblDesconto.setText(String.format("Desconto (-10%%):   R$ %.2f%s", desconto, temDesconto ? "" : "  (só PIX/Din.)"));
        lblTotal.setText(String.format("TOTAL:             R$ %.2f", total));
        lblComissao.setText(String.format("Comissão (1%%):     R$ %.2f", total * 0.01));
    }

    private void finalizarVenda() {
        int linha = tblPendentes.getSelectedRow();
        if (linha == -1) { JOptionPane.showMessageDialog(this, "Selecione um orçamento!"); return; }
        Object idOrcObj = mdlPendentes.getValueAt(linha, 0);
        String vendedor = mdlPendentes.getValueAt(linha, 1).toString();
        double subtotal = Double.parseDouble(mdlPendentes.getValueAt(linha, 3).toString().replace("R$ ", "").replace(",", "."));
        String pgto = cmbPagamento.getSelectedItem().toString();
        boolean temDesconto = pgto.equals("PIX") || pgto.equals("Dinheiro");
        double desconto = temDesconto ? subtotal * 0.10 : 0;
        double total = subtotal - desconto;
        if (JOptionPane.showConfirmDialog(this, String.format("Confirmar venda #%s?\nTotal: R$ %.2f", idOrcObj, total), "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        List<Object[]> todos = PreVendaDAO.listarPreVendasPendentes();
        boolean sucessoGeral = true;
        for (Object[] pv : todos) {
            Object idOrcPv = pv[8]; if (idOrcPv == null) idOrcPv = 0;
            if (idOrcPv.toString().equals(idOrcObj.toString())) {
                int idPrevenda = (int) pv[0]; String idProd = (String) pv[7]; int qtd = (int) pv[4]; double precoUnit = (double) pv[3];
                double subItem = precoUnit * qtd; double descItem = temDesconto ? subItem * 0.10 : 0; double totalItem = subItem - descItem;
                if (!VendaDAO.finalizarVenda(idPrevenda, pgto, subItem, totalItem, totalItem * 0.01, idProd, qtd)) { sucessoGeral = false; break; }
            }
        }
        if (sucessoGeral) {
            faturamentoDia += total; lblFaturamentoDia.setText(String.format("Faturamento do dia: R$ %.2f", faturamentoDia));
            mdlHistorico.addRow(new Object[]{ idOrcObj, vendedor, "Múltiplos Itens", String.format("R$ %.2f", subtotal), String.format("R$ %.2f", desconto), String.format("R$ %.2f", total), pgto, String.format("R$ %.2f", total * 0.01) });
            JOptionPane.showMessageDialog(this, "Venda finalizada!"); carregarPendentes();
        }
    }

    private JPanel painelTabela(String titulo, JTable tabela) {
        tabela.setFont(new Font("Monospaced", Font.PLAIN, 18));
        tabela.setRowHeight(35);
        tabela.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 18));
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel lbl = new JLabel(titulo); lbl.setFont(new Font("Monospaced", Font.BOLD, 18));
        lbl.setForeground(new Color(0, 120, 215));
        pnl.add(lbl, BorderLayout.NORTH); pnl.add(new JScrollPane(tabela), BorderLayout.CENTER); return pnl;
    }

    private JLabel rotulo(String texto) {
        JLabel lbl = new JLabel(texto); lbl.setFont(new Font("Monospaced", Font.PLAIN, 16));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); return lbl;
    }

    private void estilizarLabel(JLabel lbl, int size, boolean bold, Color cor) {
        lbl.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, size));
        lbl.setForeground(cor);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public static void main(String[] args) { EventQueue.invokeLater(() -> new TelaCaixa("Teste").setVisible(true)); }
}
