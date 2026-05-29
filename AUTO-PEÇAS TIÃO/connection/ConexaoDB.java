package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gerencia a conexão com o banco MySQL.
 *
 * COMO USAR:
 *   - Se o MySQL estiver na sua máquina local:  HOST = "localhost"
 *   - Se for a VM do curso ligada na rede:      HOST = IP da VM (ex: "192.168.56.101")
 *     Para saber o IP da VM, abra o terminal dela e digite: ip addr  (ou ifconfig)
 */
public class ConexaoDB {

    // ─── CONFIGURAÇÃO ─────────────────────────────────────────────────────────
    // Se o banco estiver no Linux Mint, mude "localhost" para o IP daquela máquina!
    private static final String HOST    = "localhost";   
    private static final int    PORTA   = 3306;
    private static final String BANCO   = "autopecas_tiao";
    private static final String USUARIO = "root";       
    private static final String SENHA   = "";           
    // ──────────────────────────────────────────────────────────────────────────

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORTA + "/" + BANCO
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8";

    private ConexaoDB() {}

    /**
     * Retorna uma conexão aberta com o banco.
     * Exemplo de uso:
     *   try (Connection conn = ConexaoDB.getConnection()) { ... }
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static boolean testar() {
        try (Connection conn = getConnection()) {
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        testar();
    }
}
