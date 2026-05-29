package model;

public class Usuario {
    private int idUsuario;
    private String username;
    private String nomePerfil;

    public Usuario(int idUsuario, String username, String nomePerfil) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.nomePerfil = nomePerfil;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getUsername() { return username; }
    public String getNomePerfil() { return nomePerfil; }
}
