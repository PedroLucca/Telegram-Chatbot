package excecoes;
public class MusicaNaoCadastrada extends Exception {
	public MusicaNaoCadastrada() {
		super("ERRO: M�sica n�o cadastrada!\n");
	}
}
