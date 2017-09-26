import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
public class Jogo extends UnicastRemoteObject implements JogoInterface {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6636057431278727855L;
	public Jogo() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public int registraJogador(String nome) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int encerraPartida(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int temPartida(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ehMinhaVez(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String obtemTabuleiro(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int posicionaPeca(int id, int pos, int orientacao) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int movePeca(int id, int posAtual, int sentidoDesl, int casasDeslocadas, int orientacao)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int obtemOponente(int id) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

}
