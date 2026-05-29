package dao;

import connection.ConexaoDB;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public static Usuario autenticar(String username, String senha) {
        String sql = """
            SELECT u.id_usuario, u.username, p.nome_perfil
            FROM usuarios u
            JOIN perfis p ON u.id_perfil = p.id_perfil
            WHERE u.username = ? AND u.senha = ?
            """;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getString("nome_perfil")
                );
            }
        } catch (SQLException e) {
            mostrarErro("autenticar", e);
        }
        return null;
    }

    public static boolean inserirUsuario(String username, String senha, String nomePerfil) {
        String sqlPerfil = "SELECT id_perfil FROM perfis WHERE nome_perfil = ?";
        String sqlInsert = "INSERT INTO usuarios (username, senha, id_perfil) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection()) {
            // Busca o id do perfil pelo nome
            int idPerfil = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlPerfil)) {
                ps.setString(1, nomePerfil);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) idPerfil = rs.getInt("id_perfil");
            }
            if (idPerfil == -1) {
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Perfil '" + nomePerfil + "' não encontrado no banco!");
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setString(1, username);
                ps.setString(2, senha);
                ps.setInt(3, idPerfil);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            mostrarErro("inserirUsuario", e);
            return false;
        }
    }

    public static boolean removerUsuario(int idUsuario) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            mostrarErro("removerUsuario", e);
            return false;
        }
    }

    // Retorna: [id_usuario, username, nome_perfil]
    public static List<Object[]> listarUsuarios() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.username, p.nome_perfil
            FROM usuarios u
            JOIN perfis p ON u.id_perfil = p.id_perfil
            ORDER BY u.username
            """;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getString("nome_perfil")
                });
            }
        } catch (SQLException e) {
            mostrarErro("listarUsuarios", e);
        }
        return lista;
    }

    private static void mostrarErro(String metodo, SQLException e) {
        System.err.println("[UsuarioDAO:" + metodo + "] " + e.getMessage());
        javax.swing.JOptionPane.showMessageDialog(null,
            "Erro no banco de dados (" + metodo + "):\n" + e.getMessage(),
            "Erro DB", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
