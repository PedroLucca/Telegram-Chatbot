package excecoes;
public class UsuarioJaCadastrado extends Exception {
	public UsuarioJaCadastrado() {
		super("ERRO: Usuario j� existente!\n");
	}
}
