package excecoes;
public class PlaylistNaoExistente extends Exception {
	public PlaylistNaoExistente(){
		super("ERRO: Playlist n�o existente!\n");
	}
}
