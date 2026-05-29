package dao;

import connection.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MontadoraDAO {

    /** Retorna lista de montadoras: [id_montadora, nome_montadora] */
    public static List<Object[]> listarMontadoras() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_montadora, nome_montadora FROM montadoras ORDER BY nome_montadora";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                lista.add(new Object[]{ rs.getInt(1), rs.getString(2) });
        } catch (SQLException e) {
            mostrarErro("listarMontadoras", e);
        }
        return lista;
    }

    private static void mostrarErro(String metodo, SQLException e) {
        System.err.println("[MontadoraDAO:" + metodo + "] " + e.getMessage());
        javax.swing.JOptionPane.showMessageDialog(null,
            "Erro no banco de dados (" + metodo + "):\n" + e.getMessage(),
            "Erro DB", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
