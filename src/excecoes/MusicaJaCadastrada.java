package excecoes;
public class MusicaJaCadastrada extends Exception {
	public MusicaJaCadastrada() {
		super("ERRO: M�sica ja cadastrado!\n");
	}
}
