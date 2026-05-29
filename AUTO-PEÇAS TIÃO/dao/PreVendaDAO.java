package dao;

import connection.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreVendaDAO {

    public static int inserirPreVenda(int idVend, String idProd, int qtd, Integer idOrc) {
        String sql = "INSERT INTO pre_vendas (id_vendedor, id_produto, quantidade, status_venda, id_orcamento) VALUES (?, ?, ?, 'PENDENTE', ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVend);
            ps.setString(2, idProd);
            ps.setInt(3, qtd);
            if (idOrc != null) ps.setInt(4, idOrc); else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            mostrarErro("inserirPreVenda", e);
        }
        return -1;
    }

    public static int gerarNovoIdOrcamento() {
        String sql = "SELECT IFNULL(MAX(id_orcamento), 0) + 1 FROM pre_vendas";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            mostrarErro("gerarNovoIdOrcamento", e);
        }
        return 1;
    }

    public static void vincularItensAoOrcamento(List<Integer> ids, int idOrc) {
        String sql = "UPDATE pre_vendas SET id_orcamento = ? WHERE id_prevenda = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int id : ids) {
                ps.setInt(1, idOrc);
                ps.setInt(2, id);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            mostrarErro("vincularItensAoOrcamento", e);
        }
    }

    public static List<Object[]> listarPreVendasPendentes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT pv.id_prevenda, u.username, " +
                     "p.nome_produto, (p.valor_custo * 1.4) AS preco_venda, " +
                     "pv.quantidade, p.codigo_referencia, m.nome_montadora, " +
                     "p.id_produto, pv.id_orcamento " +
                     "FROM pre_vendas pv " +
                     "JOIN usuarios u   ON pv.id_vendedor = u.id_usuario " +
                     "JOIN produtos p   ON pv.id_produto  = p.id_produto " +
                     "LEFT JOIN montadoras m ON p.id_montadora  = m.id_montadora " +
                     "WHERE pv.status_venda = 'PENDENTE' " +
                     "ORDER BY pv.id_orcamento, pv.id_prevenda";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String mont = rs.getString("nome_montadora");
                if (mont == null) mont = "N/A";
                lista.add(new Object[]{
                    rs.getInt("id_prevenda"),
                    rs.getString("username"),
                    rs.getString("nome_produto"),
                    rs.getDouble("preco_venda"),
                    rs.getInt("quantidade"),
                    rs.getString("codigo_referencia"),
                    mont,
                    rs.getString("id_produto"),
                    rs.getObject("id_orcamento")
                });
            }
        } catch (SQLException e) {
            mostrarErro("listarPreVendasPendentes", e);
        }
        return lista;
    }

    public static boolean removerPreVenda(int id) {
        String sql = "DELETE FROM pre_vendas WHERE id_prevenda = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            mostrarErro("removerPreVenda", e);
            return false;
        }
    }

    private static void mostrarErro(String m, SQLException e) {
        System.err.println("[PreVendaDAO:" + m + "] " + e.getMessage());
        javax.swing.JOptionPane.showMessageDialog(null,
            "Erro no banco de dados (" + m + "):\n" + e.getMessage(),
            "Erro DB", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
