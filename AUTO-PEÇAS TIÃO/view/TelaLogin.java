package view;

import dao.UsuarioDAO;
import model.SessionManager;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private JPasswordField txtSenha = new JPasswordField();
    private JLabel lblSenha = new JLabel("Senha:");
    private JLabel lblNome = new JLabel("Nome:");
    private JTextField txtNome = new JTextField();
    private JButton btnEntrar = new JButton("Entrar");
    private JPanel pnlCard = new JPanel();
    private JLabel lblTitulo = new JLabel("Auto Peças - Tião");
    private JPanel pnlDados = new JPanel();

    public TelaLogin() {
        setTitle("TelaLogin - Auto Peça Tião");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.LIGHT_GRAY);

        pnlDados.setLayout(new BoxLayout(pnlDados, BoxLayout.Y_AXIS));
        pnlDados.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlDados.setBackground(Color.WHITE);

        pnlCard.setLayout(new BoxLayout(pnlCard, BoxLayout.Y_AXIS));
        pnlCard.setBackground(Color.WHITE);
        pnlCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        pnlCard.setPreferredSize(new Dimension(500, 500));

        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(0, 120, 215));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblNome.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSenha.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNome.setFont(new Font("Monospaced", Font.PLAIN, 18));
        txtNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSenha.setFont(new Font("Monospaced", Font.PLAIN, 18));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        btnEntrar.setBackground(new Color(0, 180, 80));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnEntrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEntrar.setFont(new Font("Monospaced", Font.BOLD, 20));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEntrar.addActionListener(e -> {
            String nome = txtNome.getText();
            String senha = new String(txtSenha.getPassword());
            if (nome.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
            } else {
                Usuario usuario = UsuarioDAO.autenticar(nome, senha);
                if (usuario != null) {
                    SessionManager.getInstance().setUsuarioLogado(usuario);
                    JOptionPane.showMessageDialog(null, "Bem vindo, " + usuario.getUsername() + "!");
                    abrirTelaPorPerfil(usuario);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Usuário ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlDados.add(lblNome); pnlDados.add(Box.createVerticalStrut(10));
        pnlDados.add(txtNome); pnlDados.add(Box.createVerticalStrut(20));
        pnlDados.add(lblSenha); pnlDados.add(Box.createVerticalStrut(10));
        pnlDados.add(txtSenha); pnlDados.add(Box.createVerticalStrut(30));
        pnlDados.add(btnEntrar);

        pnlCard.add(lblTitulo); pnlCard.add(Box.createVerticalStrut(30));
        pnlCard.add(pnlDados);
        add(pnlCard);
    }

    private void abrirTelaPorPerfil(Usuario usuario) {
        String perfil = usuario.getNomePerfil().toLowerCase();
        JFrame tela = switch (perfil) {
            case "gerente"    -> new TelaGerente();
            case "vendedor"   -> new TelaVendedor();
            case "caixa"      -> new TelaCaixa(usuario.getUsername());
            case "estoquista" -> new TelaEstoquista();
            case "autoatendimento"-> new TelaAtendimento();
            default -> null;
        };
        if (tela != null) tela.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
