package excecoes;
public class UsuarioNaoCadastrado extends Exception {
	public UsuarioNaoCadastrado() {
		super("ERRO: Usuario n�o existente!\n");
	}
}
