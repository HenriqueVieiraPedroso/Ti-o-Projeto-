package dao;

import connection.ConexaoDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public static List<Object[]> listarProdutos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.*, COALESCE(m.nome_montadora, 'N/A') AS nome_montadora " +
                     "FROM produtos p " +
                     "LEFT JOIN montadoras m ON p.id_montadora = m.id_montadora " +
                     "ORDER BY p.codigo_referencia ASC";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("codigo_referencia"),
                    rs.getString("nome_produto"),
                    rs.getString("nome_montadora"),
                    rs.getDouble("valor_custo"),
                    rs.getDouble("valor_custo") * 1.4,
                    rs.getInt("quantidade_estoque"),
                    rs.getString("id_produto")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static Object[] buscarProdutoPorId(String idProd) {
        String sql = "SELECT * FROM produtos WHERE id_produto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double custo = rs.getDouble("valor_custo");
                return new Object[]{
                    rs.getString("codigo_referencia"),
                    rs.getString("nome_produto"),
                    "N/A",
                    custo,
                    custo * 1.4,
                    rs.getInt("quantidade_estoque"),
                    rs.getString("id_produto")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean inserirProduto(String idProd, int idMont, String nome, String codRef, String apl, double custo, int qtd) {
        String sql = "INSERT INTO produtos (id_produto, id_montadora, nome_produto, codigo_referencia, aplicacao_sugerida, valor_custo, quantidade_estoque) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProd);
            ps.setInt(2, idMont);
            ps.setString(3, nome);
            ps.setString(4, codRef);
            ps.setString(5, apl);
            ps.setDouble(6, custo);
            ps.setInt(7, qtd);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean atualizarProduto(String idProd, int idMont, String nome, String codRef, String apl, double custo, int qtd) {
        String sql = "UPDATE produtos SET id_montadora = ?, nome_produto = ?, codigo_referencia = ?, aplicacao_sugerida = ?, valor_custo = ?, quantidade_estoque = ? WHERE id_produto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMont);
            ps.setString(2, nome);
            ps.setString(3, codRef);
            ps.setString(4, apl);
            ps.setDouble(5, custo);
            ps.setInt(6, qtd);
            ps.setString(7, idProd);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removerProduto(String idProd) {
        String sql = "DELETE FROM produtos WHERE id_produto = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idProd);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String gerarNovoId() {
        String sql = "SELECT id_produto FROM produtos WHERE id_produto LIKE 'FT-%' ORDER BY id_produto DESC LIMIT 1";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String ultimo = rs.getString("id_produto");
                int num = Integer.parseInt(ultimo.replace("FT-", ""));
                return String.format("FT-%03d", num + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FT-300";
    }
}
