package excecoes;
public class PlaylistExistente extends Exception {
	public PlaylistExistente() {
		super("ERRO: Playlist j� existente!\n");
	}
}
