package dao;

import connection.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public static boolean finalizarVenda(int idPV, String cond,
                                          double vVenda, double vFinal,
                                          double comis,
                                          String idProd, int qtd) {
        String sqlV = "INSERT INTO vendas_concluidas (id_prevenda, condicao_pagamento, valor_venda, valor_final, comissao_vendedor) VALUES (?, ?, ?, ?, ?)";
        String sqlE = "UPDATE produtos SET quantidade_estoque = quantidade_estoque - ? WHERE id_produto = ? AND quantidade_estoque >= ?";
        String sqlS = "UPDATE pre_vendas SET status_venda = 'CONCLUIDA' WHERE id_prevenda = ?";

        try (Connection conn = ConexaoDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psV = conn.prepareStatement(sqlV);
                psV.setInt(1, idPV);
                psV.setString(2, cond);
                psV.setDouble(3, vVenda);
                psV.setDouble(4, vFinal);
                psV.setDouble(5, comis);
                psV.executeUpdate();

                PreparedStatement psE = conn.prepareStatement(sqlE);
                psE.setInt(1, qtd);
                psE.setString(2, idProd);
                psE.setInt(3, qtd);
                if (psE.executeUpdate() == 0) throw new SQLException("Sem estoque: " + idProd);

                PreparedStatement psS = conn.prepareStatement(sqlS);
                psS.setInt(1, idPV);
                psS.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            mostrarErro("finalizarVenda", e);
            return false;
        }
    }

    public static List<Object[]> relatorioComissoes() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT u.username, COUNT(vc.id_venda) AS qtd_vendas, SUM(vc.valor_final) AS faturamento, SUM(vc.comissao_vendedor) AS total_comissao " +
                     "FROM vendas_concluidas vc JOIN pre_vendas pv ON vc.id_prevenda = pv.id_prevenda JOIN usuarios u ON pv.id_vendedor = u.id_usuario " +
                     "GROUP BY u.id_usuario, u.username ORDER BY total_comissao DESC";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{ rs.getString("username"), rs.getInt("qtd_vendas"), rs.getDouble("faturamento"), rs.getDouble("total_comissao") });
            }
        } catch (SQLException e) {
            mostrarErro("relatorioComissoes", e);
        }
        return lista;
    }

    public static double[] resumoFinanceiro() {
        String sql = "SELECT IFNULL(SUM(vc.valor_final), 0) AS faturamento, IFNULL(SUM(vc.valor_final - (p.valor_custo * pv.quantidade)), 0) AS lucro, IFNULL(SUM(vc.comissao_vendedor), 0) AS comissoes " +
                     "FROM vendas_concluidas vc JOIN pre_vendas pv ON vc.id_prevenda = pv.id_prevenda JOIN produtos p ON pv.id_produto = p.id_produto";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new double[]{ rs.getDouble("faturamento"), rs.getDouble("lucro"), rs.getDouble("comissoes") };
            }
        } catch (SQLException e) {
            mostrarErro("resumoFinanceiro", e);
        }
        return new double[]{0, 0, 0};
    }

    private static void mostrarErro(String m, SQLException e) {
        System.err.println("[VendaDAO:" + m + "] " + e.getMessage());
        javax.swing.JOptionPane.showMessageDialog(null, "Erro DB (" + m + "):\n" + e.getMessage(), "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
