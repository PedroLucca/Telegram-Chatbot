package excecoes;
public class EstiloNaoCadastrado extends Exception {
	public EstiloNaoCadastrado() {
		super("ERRO: Estilo n�o cadastrado!\n");
	}
}
