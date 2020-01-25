package ufpibot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ufpimusic.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import excecoes.*;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import dao.*;


public class DjWufipibot extends TelegramLongPollingBot {
	int flag = 0;
	int flagcad = 0;
	int flaglog = 0;
	int flagalter = 0;
	int flagcriar = 0;
	int flagdel = 0;
	int flagshow = 0;
	String playalter = null;
	String playdel = null;
	String musicaalter = null;
	String playshow = null;
	static DAOPlaylists playlist = new DAOPlaylists();
	static DAOMusicasPlaylists mplaylists = new DAOMusicasPlaylists();
	static DAOMusicas acervo = new DAOMusicas();
	static DAOUsuarios usus = new DAOUsuarios();
	static DAOEstilos est = new DAOEstilos();
	ArrayList<Playlist> plays = new ArrayList<Playlist>();
	ArrayList<Musica> musics = new ArrayList<Musica>();
	Login novo = new Login();
	UsuarioNovo usunovo = new UsuarioNovo();
	
	String busca1 = "menu de play";
	String busca2 = "acervo";
	String busca3 = "exibir";
	String busca4 = "alter";
	String busca5 = "retir";
	String busca6 = "cadastr";
	String busca7 = "criar";
	String busca8 = "adiciona";
	String busca9 = "excluir";
	String busca10 = "mostrar";

	@Override
	public String getBotUsername() {
		return "DjWufipibot";
	}
	
	public String repassarP(ArrayList<String> a) {
		int i=1;
		String pow = "";
		for(String m: a) {
			pow += (i + ". "  + m + "\n");
			i++;
		}
		return pow;
	}
	
	public String repassarM(ArrayList<Musica> a) {
		int i=1;
		String pow = "";
		for(Musica m: a) {
			pow += (i + ". "  + m.getNome() + "\n");
			i++;
		}
		return pow;
	}
	
	public boolean LoginUsuario(Login v) throws UsuarioNaoCadastrado {
		usus.VerificarLogin(v);
		return true;
	}

	@Override
	public void onUpdateReceived(Update u) {
		SendMessage send = new SendMessage();
		//String nome = u.getMessage().getFrom().getFirstName();
		send.setChatId(u.getMessage().getChatId());
		String mensagem = u.getMessage().getText();
		System.out.println(u.getMessage());
//		System.out.println(u.getMessage().getContact().getPhoneNumber());
		if(((flag==0)&&(u.getMessage().getText().equals("/start")))){
			send.setText("Voc� deve fazer o login para acessar a plataforma, digite seu Usu�rio, se n�o possui "
					+ "e deseja criar seu login escreva [ cadastrar ]  e farei o seu cadastro.");
			flag=1;
		}else if((flag==0)&&((!u.getMessage().getText().equals("/start"))&&(!u.getMessage().getText().equals("/voltar")))){
			send.setText("Oi, estou ancioso para come�ar. Digite /start para interagir ou /voltar se j� tiver tentado o login!");
		}else if((flag==0)&&((u.getMessage().getText().equals("/voltar")))){
			send.setText("N�o se preocupe. Voc� apenas voltou no processo, digite seu Usu�rio, se n�o possui "
					+ "e deseja criar seu login escreva  [ cadastrar ]  e farei o seu cadastro.");
			flag=1;
		}else if(((flag==1)&&(flagcad==0)&&(flaglog==0))&&(!(u.getMessage().getText().toLowerCase().contains(busca6.toLowerCase())))){
			novo.setUsuario(mensagem);
			send.setText("Agora, digite sua senha:");
			flaglog=1;
			flagcad=0;
		}else if(((flag==1)&&(flagcad==0)&&(flaglog==1))){
			novo.setsenha(mensagem);
			try {
				LoginUsuario(novo);
				send.setText("Voc� foi logado com sucesso, " + u.getMessage().getFrom().getFirstName() + "! Meu nome � DjWufip, este � o servi�o de streaming de m�sicas para alunos " 
										+ "da UFPI. Digite /menu para verificar as op��es. ");
				flag=2;
				
				flaglog=2;
			} catch (UsuarioNaoCadastrado e) {
				send.setText("Seu login n�o foi encontrado, tente novamente digitando seu usu�rio novamente, ou se cadastre digitando /voltar se n�o "
						+ "possuir um login.");
				flaglog=0;
				flag=0;
			}
		}else if(((flag==1)&&(flagcad==0))&&(u.getMessage().getText().toLowerCase().contains(busca6.toLowerCase()))){
			send.setText("Entendi, vamos come�ar o processo de cadastro, digite o seu usu�rio:");
			flagcad=1;
		}else if((flag==1)&&(flagcad==1)){
			usunovo.setIdentificador(u.getMessage().getText());
			send.setText("Digite a sua senha:");
			flagcad=2;
		}else if((flag==1)&&(flagcad==2)){
			usunovo.setSenha(u.getMessage().getText());
			send.setText("Digite o seu email:");
			flagcad=3;
		}else if((flag==1)&&(flagcad==3)){
			usunovo.setEmail(u.getMessage().getText());
			send.setText("Digite o seu nome:");
			flagcad=4;
		}else if((flag==1)&&(flagcad==4)){
			usunovo.setNome(u.getMessage().getText());
			Assinante aux3 = new Assinante(usunovo.getIdentificador(),usunovo.getNome(),usunovo.getEmail(),usunovo.getSenha());
			try {
				usus.cadastrarUsuario(aux3);
				send.setText("Seu login foi cadastrado com sucesso " + u.getMessage().getFrom().getFirstName() + "! Meu nome � DjWufip, este � o servi�o de streaming de m�sicas para alunos "
						+ "da UFPI. Digite /menu para verificar as op��es. ");
				flag=2;
				flagcad=5;
				novo.setUsuario(usunovo.getIdentificador());
				novo.setsenha(usunovo.getSenha());
			} catch (ValorInvalido e) {
				send.setText("Parece que voc� digitou algo inv�lido e isso impossibilitou o cadastro"
						+ ", digite /voltar e tente novamente. ");
				flag=0;
				flagcad=0;
			} catch (UsuarioJaCadastrado e) {
				send.setText("Parece que o seu usu�rio j� existe e isso impossibilitou o cadastro"
						+ ", digite /voltar e tente novamente. ");
				flag=0;
				flagcad=0;
			}
		}else if((flag==2)&&(u.getMessage().getText().equals("/menu"))){
			send.setText("Boa escolha! Veja o menu de fun��es:\n\n 1.Exibir acervo\n 2.Menu de playlists");
			flag=3;
		}else if((flag>2)&&(u.getMessage().getText().equals("/menu"))){
			send.setText("Veja o menu de fun��es:\n\n 1.Exibir acervo\n 2.Menu de playlists");
			flag=3;
		}else if((flag==3)&&((u.getMessage().getText().toLowerCase().contains(busca2.toLowerCase()))||(u.getMessage().getText().equals("1")))){
			ArrayList<String> aux5 = acervo.acervo();
			String acv = repassarP(aux5);
			send.setText("Acervo de m�sicas:\n\n" + acv);
			flag=2;
		}else if((flag==3)&&(((u.getMessage().getText().toLowerCase().contains(busca1.toLowerCase())))||(u.getMessage().getText().equals("2")))){
			send.setText("Entendi!\n Aqui est� o menu de playlists:\n\n 1.Exibir suas playlists\n 2.Criar playlist\n 3.Alterar\n 4.Excluir\n 5.Mostrar m�sicas de uma playlist");
			flag=4;
		}else if((flag>3)&&((u.getMessage().getText().toLowerCase().contains(busca1.toLowerCase())))){
			send.setText("� pra j�, aqui est� o menu de playlists:\n\n 1.Exibir suas playlists\n 2.Criar playlist\n 3.Alterar\n 4.Excluir\n 5.Mostrar m�sicas de uma playlist");
			flag=4;
		}else if((flag==4)&&((u.getMessage().getText().toLowerCase().contains(busca3.toLowerCase()))||(u.getMessage().getText().equals("1")))){
			try {
				plays = playlist.pesquisarPlaylistsUsuario(novo.getUsuario());
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			}
			String repass =  null;
			ArrayList<String> aux0 = new ArrayList<String>();
			try {
				aux0 = playlist.pesquisarPlay(novo.getUsuario());
				repass = repassarP(aux0);
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			}
			if(aux0.size()==0) {
				send.setText("Hmm...que pena, parece que voc� ainda n�o criou nenhuma playlist, v� ao menu de playlists e crie!");
				flag=3;
			}else {
				send.setText("Aqui est�o as suas playlists:\n\n" + repass);
				flag=3;
			}
		}else if((flag==4)&&((u.getMessage().getText().toLowerCase().contains(busca7.toLowerCase()))||(u.getMessage().getText().equals("2")))){
			send.setText("Certo! Digite o nome da playlist que deseja criar:");
			flagcriar=1;
		}else if((flag==4)&&(flagcriar==1)){
			try {
				playlist.criarPlaylist(novo.getUsuario(), mensagem);
				send.setText("A� sim! Playlist criada com sucesso, voc� pode verificar ela na op��o de exibir as playlists");
			} catch (ValorInvalido e) {
				send.setText("Parece que voc� digitou um nome inv�lido para a playlist...tente novamente");
				flag=3;
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			} catch (PlaylistExistente e) {
				send.setText("Opa pera�...essa playlist j� existe,tente novamente");
				flag=3;
			}
			flagcriar=0;
		}else if((flag==4)&&((u.getMessage().getText().toLowerCase().contains(busca4.toLowerCase())))||(u.getMessage().getText().equals("3"))){
			String repass =  null;
			ArrayList<String> aux0 = new ArrayList<String>();
			try {
				aux0 = playlist.pesquisarPlay(novo.getUsuario());
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			}
			if(aux0.size()==0) {
				send.setText("N�o h� nenhuma playlist para ser alterada. Crie uma!");
				flagalter=0;
				flag=3;
			}else {
				repass = repassarP(aux0);
				send.setText("Beleza!\n Digite o nome da playlist que deseja alterar:\n" + repass);
				flagalter=1;
			}
		}else if(((flag==4)&&(flagalter==1))){
			try {
				playlist.pesquisarPlaylist(mensagem, novo.getUsuario());
				playalter = mensagem;
				send.setText("Deseja adicionar ou excluir uma m�sica na playlist " + playalter + "?");
				flagalter=2;
			} catch (PlaylistNaoExistente e) {
				send.setText("A playlist que voc� digitou n�o existe, tente novamente.");
				flagalter=0;
				flag=3;
			}
		}else if(((flag==4)&&(flagalter==2))&&((u.getMessage().getText().toLowerCase().contains(busca8.toLowerCase())))){
			ArrayList<String> aux5 = acervo.acervo();
			String acv = repassarP(aux5);
			send.setText("Digite o nome da m�sica que deseja adicionar � playlist " + playalter + ": \n\n" + acv);
			flagalter=3;
		}else if(((flag==4)&&(flagalter==3))){
			musicaalter = mensagem;
			try {
				Musica pof = acervo.pesquisarMusica(musicaalter);
				try {
					mplaylists.adicionarMusicaPlaylist(novo.getUsuario(), playalter, pof.getArtista() , musicaalter);
					send.setText("A m�sica " + musicaalter + " foi adicionado � "+ playalter + "!");
					flagalter=0;
					flag=3;
				} catch (UsuarioNaoCadastrado e) {
					e.printStackTrace();
				} catch (PlaylistNaoExistente e) {
					e.printStackTrace();
				} catch (MusicaJaCadastrada e) {
					send.setText("Hm...Parece que a m�sica que voc� digitou j� est� nessa playlist, tente com outra!");
					flagalter=0;
					flag=3;
				}
			}catch (MusicaNaoCadastrada e){
				send.setText("Hm...Parece que a m�sica que voc� digitou n�o est� no acervo, tente de novo.");
				flagalter=0;
				flag=3;
			}
		}else if(((flag==4)&&(flagalter==2))&&((u.getMessage().getText().toLowerCase().contains(busca9.toLowerCase())))){
			Playlist aux8 = null;
			try {
				aux8 = playlist.pesquisaPlaylistUsuario(novo.getUsuario(), playalter);
				if(aux8.getMusicas().size()==0) {
					send.setText("N�o h� nenhuma m�sica nessa playlist, tente com outra!");
				}else {
					String acv = repassarM(aux8.getMusicas());
					send.setText("Digite o nome da m�sica que deseja excluir da playlist " + playalter + "?\n" + acv);
					flagalter=4;
				}
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			} catch (PlaylistNaoExistente e) {
				e.printStackTrace();
			}
		}else if(((flag==4)&&(flagalter==4))){
			musicaalter = mensagem;
			try {
				playlist.deletarUmaMusica(musicaalter);
				send.setText("A m�sica "+ musicaalter + " que voc� escolheu foi retirada com sucesso!");
				flagalter=0;
				flag=3;
			} catch (MusicaNaoCadastrada e) {
				send.setText("A m�sica que voc� escolheu n�o existe no acervo, tente denovo!");
				flagalter=0;
				flag=3;
			}
		}else if((flag==4)&&((u.getMessage().getText().toLowerCase().contains(busca9.toLowerCase())))||(u.getMessage().getText().equals("4"))){
			String repass =  null;
			ArrayList<String> aux0 = new ArrayList<String>();
			try {
				aux0 = playlist.pesquisarPlay(novo.getUsuario());
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			}
			repass = repassarP(aux0);
			send.setText("Beleza!\n Digite o nome da playlist que deseja excluir:\n" + repass);
			flagdel=1;
		}else if(((flag==4)&&(flagdel==1))){
			try {
				playlist.pesquisarPlaylist(mensagem, novo.getUsuario());
				playdel = mensagem;
				mplaylists.excluirMusicasUmaPlaylist(playdel);
				playlist.excluirUmaPlaylist(playdel);
				send.setText("Pronto, "+ playdel + " exclu�da com sucesso.");
				flagdel=0;
				flag=3;
			} catch (PlaylistNaoExistente e) {
				send.setText("A playlist que voc� digitou n�o existe, tente novamente.");
				flagdel=0;
				flag=3;
			}
		}else if((flag==4)&&(((u.getMessage().getText().toLowerCase().contains(busca10.toLowerCase())))||(u.getMessage().getText().equals("5")))){
			String repass =  null;
			ArrayList<String> aux0 = new ArrayList<String>();
			try {
				aux0 = playlist.pesquisarPlay(novo.getUsuario());
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			}
			if(aux0.size()==0) {
				send.setText("Voc� ainda n�o possui nenhuma playlist, crie uma!");
				flag=3;
				flagshow=0;
			}else {
				repass = repassarP(aux0);
				send.setText("Beleza!\n Digite o nome da playlist que deseja exibir as m�sicas:\n" + repass);
				flagshow=1;
			}
		}else if(((flag==4)&&(flagshow==1))){
			playshow = mensagem;
			Playlist aux8 = null;
			try {
				aux8 = playlist.pesquisaPlaylistUsuario(novo.getUsuario(), playshow);
				if(aux8.getMusicas().size()==0) {
					send.setText("Ainda n�o h� nenhuma m�sica nessa playlist, adicione!");
				}else {
					String acv = repassarM(aux8.getMusicas());
					send.setText("M�sicas de " + playshow + ":\n" + acv);
					flagshow=0;
					flag=3;
				}
			} catch (UsuarioNaoCadastrado e) {
				e.printStackTrace();
			} catch (PlaylistNaoExistente e) {
				send.setText("A playlist que voc� digitou n�o existe , tente novamente.");
				flagshow=0;
				flag=3;
			}

		}else{
			send.setText("N�o entendi...\nDeixa eu te dar uma dica, leia direitinho o que se pede, para interagirmos melhor.");
			flag=3;
		}
		
		try {
			execute(send);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getBotToken() {
		return "841426016:AAEjNWzbesSW-HAaqAlDBqUlizfYTYYbTsY";
	}

	public static void main(String[] args) throws ValorInvalido, UsuarioNaoCadastrado, MusicaJaCadastrada, 
	EstiloNaoCadastrado, UsuarioJaCadastrado, EstiloJaCadastrado, PlaylistExistente, PlaylistNaoExistente, MusicaNaoCadastrada {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBot = new TelegramBotsApi();
		DjWufipibot bot = new DjWufipibot();
		usus.limparBanco();
		Usuario assinante = new Assinante("Pedro", "Antonio", "antionio@mail.com", "senha1234");
		Usuario artista = new Artista("zeca", "Zeca Baleiro", "zecabaleiro@mail.com", "z3qu1nh4");
		Artista artista2 = new Artista("alceu", "Alceu loco", "contato@js.com", "jj90");
		ArrayList<Artista> artistas = new ArrayList<Artista>();
		artistas.add(artista2);
		Usuario banda = new Banda("ForroBoys", "Forro Boys", "contato@fb.com", "forrozinho", artistas);
		usus.cadastrarUsuario(banda);
		usus.cadastrarUsuario(assinante);
		usus.cadastrarUsuario(artista);
		est.cadastrarEstilo("MPB");
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.set(2019, 02, 20);
		long milisegundos = gregorianCalendar.getTimeInMillis();
		Date lancamento = new Date(milisegundos);
		acervo.adicionarMusica("zeca", "Telegrama", "MPB", "https://www.ufpimusic/zeca/telegrama.mp3", 426, lancamento);
		acervo.adicionarMusica("zeca", "Coracao Bobo", "MPB", "https://www.ufpimusic/alceu/telegrama.mp3", 412, lancamento);
		acervo.adicionarMusica("zeca", "Babylon", "MPB", "https://www.ufpimusic/zeca/babylon.mp3", 440, lancamento);
		acervo.adicionarMusica("zeca", "La Belle de Jour", "MPB", "https://www.ufpimusic/alceu/laBelleDeJour.mp3", 430, lancamento);
		playlist.criarPlaylist("Pedro", "Classicos");
		playlist.criarPlaylist("Pedro", "Corotes");
		mplaylists.adicionarMusicaPlaylist("Pedro", "Classicos", "zeca", "Telegrama");
		mplaylists.adicionarMusicaPlaylist("Pedro", "Classicos", "zeca", "La Belle de Jour");
		//
		try {
			telegramBot.registerBot(bot);
		} catch (TelegramApiRequestException e) {
			System.out.println("Erro no Bot");
			e.printStackTrace();
		}
	}
}