import java.rmi.Naming;
import java.util.Scanner;

public class JogoCliente {

	public static void main(String[] args) {
		try{
			JogoInterface j = (JogoInterface)Naming.lookup("//localhost/Jogo");
			System.out.println("Comecou");
			Scanner entrada = new Scanner(System.in);
			Scanner entrada2 = new Scanner(System.in);
			Scanner posE = new Scanner(System.in);
			Scanner oriE = new Scanner(System.in);
			Scanner posMovE = new Scanner(System.in);
			Scanner senDesE = new Scanner(System.in);
			Scanner numCasDesE = new Scanner(System.in);
			Scanner oriMovE = new Scanner(System.in);
			String nome = "";
			System.out.println("Informe seu nome: ");
			nome = entrada.next();
			int id = j.registraJogador(nome);
			int continua = 1;
			int opcao = 0;
			while(continua!=0){
				System.out.println("O que voce gostaria de fazer(escolha um numero): ");
				System.out.println("1 - Obter oponente");
				System.out.println("2 - Tem partida");
				System.out.println("3 - Obter tabuleiro");
				System.out.println("4 - Eh minha vez");
				System.out.println("5 - Posiciona peca");
				System.out.println("6 - Move peca");
				System.out.println("7 - Encerrar partida");
				opcao = entrada2.nextInt();
				switch(opcao){
					case 1: System.out.println(j.obtemOponente(id));
						break;
					case 2: System.out.println(j.temPartida(id));
						break;
					case 3: System.out.println(j.obtemTabuleiro(id));
						break;
					case 4:System.out.println(j.ehMinhaVez(id)); 
						break;
					case 5: System.out.println("Qual a posicao desejada (0a8): ");
							int pos = posE.nextInt();
							System.out.println("Qual a orientacao da peca (0ou1): ");
							int ori = oriE.nextInt();
							System.out.println(j.posicionaPeca(id, pos, ori));
						break;
					case 6: System.out.println("Qual pos voce quer mover (0a8): ");
							int posMov = posMovE.nextInt();
							System.out.println("Sentido que voce quer mover (0a8): ");
							int senDes = senDesE.nextInt();
							System.out.println("Numero de casas deslocadas (0,1ou2): ");
							int numDes = numCasDesE.nextInt();
							System.out.println("Orientacao da peca (0ou1):");
							int oriMov = oriMovE.nextInt();
							System.out.println(j.movePeca(id, posMov, senDes, numDes, oriMov));
						break;
					case 7:System.out.println(j.encerraPartida(id)); 
						continua = 0;
						break;
					default: System.out.println("Opcao invalida!");
							break;
				}
			}
		}catch (Exception e){
			System.out.println ("Falha JogoCliente!");
			e.printStackTrace();
		}

	}

}
