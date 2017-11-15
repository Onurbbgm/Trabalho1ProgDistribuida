import java.rmi.Naming;
import java.rmi.RemoteException;

public class JogoServidor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI registry ready.");			
		} catch (RemoteException e) {
			System.out.println("RMI registry already running.");			
		}
		try{
			char[][] tabuleiro = new char[3][3];
			Naming.rebind("Jogo", new Jogo(new Jogador(null,0,0,0,0), new Jogador(null,0,0,0,0),tabuleiro));
			System.out.println("JogoServidor esta pronto!");
		}catch (Exception e){
			System.out.println("Falha JogoServidor!");
			e.printStackTrace();
		}
	}

}
