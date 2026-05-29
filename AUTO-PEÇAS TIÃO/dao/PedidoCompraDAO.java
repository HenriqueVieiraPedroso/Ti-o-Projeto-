package dao;

import connection.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoCompraDAO {

    public static boolean inserirPedidoCompra(String fornecedor, String peca, int quantidade, int idGerente) {
        String sql = """
            INSERT INTO pedidos_compra (fornecedor, nome_peca, quantidade, id_gerente)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fornecedor);
            ps.setString(2, peca);
            ps.setInt(3, quantidade);
            ps.setInt(4, idGerente);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            mostrarErro("inserirPedidoCompra", e);
            return false;
        }
    }

    public static boolean removerPedido(int idPedido) {
        String sql = "DELETE FROM pedidos_compra WHERE id_pedido = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            mostrarErro("removerPedido", e);
            return false;
        }
    }

    // Retorna: [id_pedido, fornecedor, peca, quantidade, username_gerente, data]
    public static List<Object[]> listarPedidosPendentes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = """
            SELECT pc.id_pedido, pc.fornecedor, pc.nome_peca, pc.quantidade,
                   u.username AS gerente, pc.data_pedido
            FROM pedidos_compra pc
            JOIN usuarios u ON pc.id_gerente = u.id_usuario
            ORDER BY pc.data_pedido DESC
            """;
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_pedido"),
                    rs.getString("fornecedor"),
                    rs.getString("nome_peca"),
                    rs.getInt("quantidade"),
                    rs.getString("gerente"),
                    rs.getString("data_pedido")
                });
            }
        } catch (SQLException e) {
            mostrarErro("listarPedidosPendentes", e);
        }
        return lista;
    }

    private static void mostrarErro(String metodo, SQLException e) {
        System.err.println("[PedidoCompraDAO:" + metodo + "] " + e.getMessage());
        javax.swing.JOptionPane.showMessageDialog(null,
            "Erro no banco de dados (" + metodo + "):\n" + e.getMessage(),
            "Erro DB", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
