import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

class Jogador{
	public String nome;
	public int id;
	public int numJogador;
	public int vez;
	public int estaNumPartida;
	public Jogador(String nome, int id, int numJogador, int vez, int estaNumPartida){
		this.nome = nome;
		this.id = id;
		this.numJogador = numJogador;
		this.vez = vez;
		this.estaNumPartida = estaNumPartida;
	}
}

class Partida{
	public int numParticipantes;
	public Partida(int numParticipantes){
		this.numParticipantes = numParticipantes;
	}
}

public class Jogo extends UnicastRemoteObject implements JogoInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6636057431278727855L;
	
	private ArrayList<Jogador> jogadores = new ArrayList<Jogador>();
	private ArrayList<Jogo> jogos = new ArrayList<Jogo>();
	private char[][] tabuleiro = new char[3][3];
	private char peca;
	private static int idJogador = 1;
	private static int idJogo = 1;
	private int numParticipantes;
	private Jogador j;
	private Jogador j2;
	private long contTemBuscaJog;
	
	
	public Jogo(Jogador j1, Jogador j2, char[][] tabuleiro /*,int numParticipantes, Jogador j, int idJogo*/) throws RemoteException {
		this.numParticipantes = numParticipantes;
		j = j1;
		this.j2 = j2;
		//this.idJogo = idJogo;
//		jogadores = new ArrayList<Jogador>();
//		jogos = new ArrayList<Jogo>();
		this.tabuleiro = tabuleiro;
		for(int linha=0 ; linha<3 ; linha++){
			for(int coluna=0 ; coluna<3 ; coluna++){
				tabuleiro[linha][coluna]='.';
			}      
		}
            
	}
	
	public void setNumParticipantes(int numParticipantes){
		this.numParticipantes = numParticipantes;
	}
	
	@Override
	public int registraJogador(String nome) throws RemoteException {
		int id = idJogador;
		if(jogadores.size()>=500){
			return -2;//numero maximo de jogadores atingidos
		}
		for(int i = 0; i<jogadores.size(); i++){
			if(nome.equals(jogadores.get(i).nome)){
				return -1; //Usuariao ja cadastrado
			}
		}
		Jogador j = new Jogador(nome,id,0,0,0);
		jogadores.add(j);
		//char[][] tab = tabuleiro;
		//Jogo jog = new Jogo(j, null);
		//jogos.add(jog);
		idJogador++;
		contTemBuscaJog = System.currentTimeMillis();
		return id;
	}

	@Override
	public int encerraPartida(int id) throws RemoteException {
		int acabou = ehMinhaVez(id);
		if(acabou !=1 && acabou != -1 && acabou != -2){
			for(int i = 0; i<jogos.size(); i++){
				if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes == 2){
					jogos.remove(i);//FALTA REMOVER OS JOGADORES TB DO ARRAY
					return 0;//partida encerradad com sucesso
				}
			}		
		}
		
		return 0;
	}

	@Override
	public int temPartida(int id) throws RemoteException {
		for(int i = 0; i<jogos.size(); i++){
			if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes == 1){
				return 0;//ainda nao ha partida
			}
			if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes == 2){
				if(jogos.get(i).j.numJogador!=0){
					return jogos.get(i).j.numJogador;
				}
				
				//for(int l = 0; l<jogos.size(); l++){
					//Para procurar pelo outro jogador que esta associado ao mesmo jogo pelo idJogo
					//if(jogos.get(i).j2 == jogos.get(i).idJogo && jogos.get(l).j.id != id){
						if(jogos.get(i).j2.numJogador==1){
							jogos.get(i).j.numJogador = 2;
							jogos.get(i).j.vez = 0;
							return jogos.get(i).j.numJogador;
						}
						if(jogos.get(i).j2.numJogador==2){
							jogos.get(i).j.numJogador = 1;
							jogos.get(i).j.vez = 1;
							return jogos.get(i).j.numJogador;
						}else{
							Random rand = new Random();
						    int randomNum = rand.nextInt((2 - 1) + 1) + 1;
						    jogos.get(i).j.numJogador = randomNum;
						    if(randomNum==1){
						    	jogos.get(i).j.vez = 1;
						    	jogos.get(i).j2.numJogador = 2;
						    	jogos.get(i).j2.vez = 0;
						    }
						    if(randomNum == 2){
						    	jogos.get(i).j2.vez = 1;
						    	jogos.get(i).j2.numJogador = 1;
						    	jogos.get(i).j.vez = 0;
						    }
						    System.out.println("NumJogador j1: " + jogos.get(i).j.numJogador);
						    System.out.println("NumJogador j2: " + jogos.get(i).j2.numJogador);
							return randomNum;
						}
					//}
				//}	
			}
		}
		//placeholder, ainda tem que ser implementado
		//int tempo = 1;
		//if(tempo==0){
			//return -2; //tempo de espera esgotado
		//}else{
			return -1;//erro
		//}
	}

	
	@Override
	public int ehMinhaVez(int id) throws RemoteException {
		int tempo1 = 1; //placeholder, para ser implementado timeout
		int tempo2 = 1; //placeholder, para ser implementado timeout
		for(int i = 0; i<jogos.size(); i++){
			if((jogos.get(i).j.id==id && jogos.get(i).numParticipantes !=2) || (jogos.get(i).j2.id==id && jogos.get(i).numParticipantes !=2)){
				return -2; //ainda nao tem 2 jogadores
			}
			if((jogos.get(i).j.id==id && venceu(id)!=0 && venceu(id)==jogos.get(i).j.numJogador)||(jogos.get(i).j2.id==id && venceu(id)!=0 && venceu(id)==jogos.get(i).j2.numJogador)){
				return 2; //eh o vencedor
			}
			if((jogos.get(i).j.id==id && venceu(id)!=0 && venceu(id)!=jogos.get(i).j.numJogador) || (jogos.get(i).j2.id==id && venceu(id)!=0 && venceu(id)!=jogos.get(i).j2.numJogador)){
				return 3; //eh o perdedor
			}
			if(jogos.get(i).j.id == id && tempo1 == 0){ //placeholder
				return 6; //perdedor por WO
			}
			if(jogos.get(i).j.id == id && tempo2 == 0){ //placeholder
				return 5;//vencedor por WO
			}
			if((jogos.get(i).j.id == id && jogos.get(i).j.vez == 1) || (jogos.get(i).j2.id == id && jogos.get(i).j2.vez == 1)){
				return 1;//sim
			}
			if((jogos.get(i).j.id == id && jogos.get(i).j.vez == 0) || (jogos.get(i).j2.id == id && jogos.get(i).j2.vez == 0)){
				return 0;//nao
			}
			
		}
		return -1;//erro
	}

	@Override
	public String obtemTabuleiro(int id) throws RemoteException {
		for(int i = 0; i<jogos.size(); i++){
			if(jogos.get(i).j.id==id || jogos.get(i).j2.id==id ){
				String tab = "";
		        for(int linha=0 ; linha<3 ; linha++){
		        
		            for(int coluna=0 ; coluna<3 ; coluna++){
		                
		                if(jogos.get(i).tabuleiro[linha][coluna]== '.'){
		                    tab += " . ";
		                }
		                if(jogos.get(i).tabuleiro[linha][coluna]=='c'){
		                    tab += " c ";
		                }
		                if(jogos.get(i).tabuleiro[linha][coluna]=='C'){
		                    tab += " C ";
		                }
		                if(jogos.get(i).tabuleiro[linha][coluna]=='e'){
		                    tab += " e ";
		                }
		                if(jogos.get(i).tabuleiro[linha][coluna]=='E'){
		                    tab += " E ";
		                }
		                
		                if(coluna==0 || coluna==1)
		                    tab += "|";
		            }
		            tab += "\n";
		        }
				return tab;
			}
		}
		
			return "";//erro
		
//		String tab = "";
//        for(int linha=0 ; linha<3 ; linha++){
//        
//            for(int coluna=0 ; coluna<3 ; coluna++){
//                
//                if(tabuleiro[linha][coluna]== '.'){
//                    tab += " . ";
//                }
//                if(tabuleiro[linha][coluna]=='c'){
//                    tab += " c ";
//                }
//                if(tabuleiro[linha][coluna]=='C'){
//                    tab += " C ";
//                }
//                if(tabuleiro[linha][coluna]=='e'){
//                    tab += " e ";
//                }
//                if(tabuleiro[linha][coluna]=='E'){
//                    tab += " E ";
//                }
//                
//                if(coluna==0 || coluna==1)
//                    tab += "|";
//            }
//            tab += "\n";
//        }
//		return tab;
	}

	@Override
	public int posicionaPeca(int id, int pos, int orientacao) throws RemoteException {
		int vez = ehMinhaVez(id);
		if(vez == 1 && !tabuleiroCheio(id)){
			for(int i = 0; i<jogos.size(); i++){
				if((jogos.get(i).j.id == id || jogos.get(i).j2.id == id) && jogos.get(i).numParticipantes!=2){
					return -2;//ainda nao tem dois jogadores na partida
				}
				if((jogos.get(i).j.id == id || jogos.get(i).j2.id == id) && jogos.get(i).numParticipantes==2){
					//caso seja o jogador 1(claras)
					if((jogos.get(i).j.numJogador == 1 && jogos.get(i).j.vez ==1) || (jogos.get(i).j2.numJogador == 1 && jogos.get(i).j2.vez == 1)){
						if(pos == 0 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[0][0] == '.'){
								jogos.get(i).tabuleiro[0][0] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
//								for(int l = 0; l<jogos.size(); l++){
//									if(jogos.get(l).idJogo == jogos.get(i).idJogo && jogos.get(l).j.id != id){
//										jogos.get(l).j.vez = 1;
//									}
//								}
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 0 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[0][0] == '.'){
								jogos.get(i).tabuleiro[0][0] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
//								for(int l = 0; l<jogos.size(); l++){
//									if(jogos.get(l).idJogo == jogos.get(i).idJogo && jogos.get(l).j.id != id){
//										jogos.get(l).j.vez = 1;
//									}
//								}
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[0][1] == '.'){
								jogos.get(i).tabuleiro[0][1] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
//								for(int l = 0; l<jogos.size(); l++){
//									if(jogos.get(l).idJogo == jogos.get(i).idJogo && jogos.get(l).j.id != id){
//										jogos.get(l).j.vez = 1;
//									}
//								}
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[0][1] == '.'){
								jogos.get(i).tabuleiro[0][1] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;							
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[0][2] == '.'){
								jogos.get(i).tabuleiro[0][2] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[0][2] == '.'){
								jogos.get(i).tabuleiro[0][2] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[1][0] == '.'){
								jogos.get(i).tabuleiro[1][0] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[1][0] == '.'){
								jogos.get(i).tabuleiro[1][0] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[1][1] == '.'){
								jogos.get(i).tabuleiro[1][1] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[1][1] == '.'){
								jogos.get(i).tabuleiro[1][1] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[1][2] == '.'){
								jogos.get(i).tabuleiro[1][2] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[1][2] == '.'){
								jogos.get(i).tabuleiro[1][2] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[2][0] == '.'){
								jogos.get(i).tabuleiro[2][0] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[2][0] == '.'){
								jogos.get(i).tabuleiro[2][0] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[2][1] == '.'){
								jogos.get(i).tabuleiro[2][1] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[2][1] == '.'){
								jogos.get(i).tabuleiro[2][1] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(jogos.get(i).tabuleiro[2][2] == '.'){
								jogos.get(i).tabuleiro[2][2] = 'C';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(jogos.get(i).tabuleiro[2][2] == '.'){
								jogos.get(i).tabuleiro[2][2] = 'c';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						
					}
					//caso seja o jogador 2(escuras)
					if((jogos.get(i).j.numJogador == 2 && jogos.get(i).j.vez ==1) || (jogos.get(i).j2.numJogador == 2 && jogos.get(i).j2.vez == 1)){
						if(pos == 0 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[0][0] == '.'){
								jogos.get(i).tabuleiro[0][0] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 0 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[0][0] == '.'){
								jogos.get(i).tabuleiro[0][0] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[0][1] == '.'){
								jogos.get(i).tabuleiro[0][1] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[0][1] == '.'){
								jogos.get(i).tabuleiro[0][1] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[0][2] == '.'){
								jogos.get(i).tabuleiro[0][2] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[0][2] == '.'){
								jogos.get(i).tabuleiro[0][2] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[1][0] == '.'){
								jogos.get(i).tabuleiro[1][0] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[1][0] == '.'){
								jogos.get(i).tabuleiro[1][0] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[1][1] == '.'){
								jogos.get(i).tabuleiro[1][1] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[1][1] == '.'){
								jogos.get(i).tabuleiro[1][1] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[1][2] == '.'){
								jogos.get(i).tabuleiro[1][2] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[1][2] == '.'){
								jogos.get(i).tabuleiro[1][2] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[2][0] == '.'){
								jogos.get(i).tabuleiro[2][0] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[2][0] == '.'){
								jogos.get(i).tabuleiro[2][0] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).tabuleiro[2][1] == '.'){
								jogos.get(i).tabuleiro[2][1] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[2][1] == '.'){
								jogos.get(i).tabuleiro[2][1] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(jogos.get(i).jogos.get(i).tabuleiro[2][2] == '.'){
								jogos.get(i).tabuleiro[2][2] = 'E';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(jogos.get(i).tabuleiro[2][2] == '.'){
								jogos.get(i).tabuleiro[2][2] = 'e';
								if(jogos.get(i).j2.vez == 0){
									jogos.get(i).j.vez = 0;
									jogos.get(i).j2.vez = 1;
									return 1;
								}
								jogos.get(i).j2.vez = 0;
								jogos.get(i).j.vez = 1;
								return 1;
							}else{
								return -1;
							}
						}
						
					}
					
				}
			}
		}
		if(vez==0){
			return -3;//nao eh a sua vez
		}
		if(tabuleiroCheio(id)){
			return -4;//nao tem mais posicoes livres disponiveis
		}
		
		return -1;
	}

	@Override
	public int movePeca(int id, int posAtual, int sentidoDesl, int casasDeslocadas, int orientacao)
			throws RemoteException {
		int vez = ehMinhaVez(id);
		if(vez != 1){
			return -3;//nao eh a vez do jogador
		}
		if(!tabuleiroCheio(id)){
			return -4; //tabuleiro nao esta cheio
		}
		
		for(int i = 0; i<jogos.size(); i++){
			if((jogos.get(i).j.id == id || jogos.get(i).j2.id == id) && jogos.get(i).numParticipantes !=2){
				return -2;//partida nao inicada ainda, nao ha dois jogadores
			}
			if((jogos.get(i).j.id == id || jogos.get(i).j2.id == id) && jogos.get(i).numParticipantes == 2){
				//claras
				if((jogos.get(i).j.numJogador == 1 && jogos.get(i).j.vez == 1) || (jogos.get(i).j2.numJogador == 1 && jogos.get(i).j2.vez == 1)){
					//posAtual 0 movimento perpendicular
					if(posAtual == 0 && jogos.get(i).tabuleiro[0][0] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[0][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							jogos.get(i).j.vez = 0;
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 0 movimento diagonal
					if(posAtual == 0 && jogos.get(i).tabuleiro[0][0] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 8  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 8  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 1 movimento perpendicular
					if(posAtual == 1 && jogos.get(i).tabuleiro[0][1] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3   && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'c';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 1 movimento diagonal
					if(posAtual == 1 && jogos.get(i).tabuleiro[0][1] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 6   && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 2 movimento perpendicular
					if(posAtual == 2 && jogos.get(i).tabuleiro[0][2] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 2 movimento diagonal
					if(posAtual == 2 && jogos.get(i).tabuleiro[0][2] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 3 movimento perpendicular
					if(posAtual == 3 && jogos.get(i).tabuleiro[1][0] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 3 movimento diagonal
					if(posAtual == 3 && jogos.get(i).tabuleiro[1][0] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtul 4 movimento perpendicular
					if(posAtual == 4 && jogos.get(i).tabuleiro[1][1] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 4 && jogos.get(i).tabuleiro[1][1] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}						
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 5 movimento perpendicular
					if(posAtual == 5 && jogos.get(i).tabuleiro[1][2] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 5 movimento diagonal
					if(posAtual == 5 && jogos.get(i).tabuleiro[1][2] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 6 movimento perpendicular
					if(posAtual == 6 && jogos.get(i).tabuleiro[2][0] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[2][0] = 'c';//diagonal
							jogos.get(i).j.vez = 0;
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual movimento diagonal
					if(posAtual == 6 && jogos.get(i).tabuleiro[2][0] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular							
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 7 movimento perpendicular
					if(posAtual == 7 && jogos.get(i).tabuleiro[2][1] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3   && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'c';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 7 movimento diagonal
					if(posAtual == 7 && jogos.get(i).tabuleiro[0][1] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 8 movimento perpendicular
					if(posAtual == 8 && jogos.get(i).tabuleiro[2][2] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[2][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 8 movimento diagonal
					if(posAtual == 8 && jogos.get(i).tabuleiro[2][2] == 'c'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[2][2] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'C';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'c';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
				}
				//ESCURAS
				if((jogos.get(i).j.numJogador == 2 && jogos.get(i).j.vez == 1) || (jogos.get(i).j2.numJogador == 2 && jogos.get(i).j2.vez == 1) ){
					//posAtual 0 movimento perpendicular
					if(posAtual == 0 && jogos.get(i).tabuleiro[0][0] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[0][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 0 movimento diagonal
					if(posAtual == 0 && jogos.get(i).tabuleiro[0][0] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 8  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 8  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 1 movimento perpendicular
					if(posAtual == 1 && jogos.get(i).tabuleiro[0][1] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3   && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'e';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if( sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 1 movimento diagonal
					if(posAtual == 1 && jogos.get(i).tabuleiro[0][1] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 6   && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 2 movimento perpendicular
					if(posAtual == 2 && jogos.get(i).tabuleiro[0][2] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							jogos.get(i).j.vez = 0;
							for(int l = 0; l<jogos.size(); l++){
								if(jogos.get(l).idJogo == jogos.get(i).idJogo && jogos.get(l).j.id != id){
									jogos.get(l).j.vez = 1;
								}
							}
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 2 movimento diagonal
					if(posAtual == 2 && jogos.get(i).tabuleiro[0][2] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[0][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 3 movimento perpendicular
					if(posAtual == 3 && jogos.get(i).tabuleiro[1][0] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 3 movimento diagonal
					if(posAtual == 3 && jogos.get(i).tabuleiro[1][0] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular							
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtul 4 movimento perpendicular
					if(posAtual == 4 && jogos.get(i).tabuleiro[1][1] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 4 && jogos.get(i).tabuleiro[1][1] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}						
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][1] = '.';
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 5 movimento perpendicular
					if(posAtual == 5 && jogos.get(i).tabuleiro[1][2] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 5 movimento diagonal
					if(posAtual == 5 && jogos.get(i).tabuleiro[1][2] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[1][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 6 movimento perpendicular
					if(posAtual == 6 && jogos.get(i).tabuleiro[2][0] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[2][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual movimento diagonal
					if(posAtual == 6 && jogos.get(i).tabuleiro[2][0] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][0] = '.';
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 7 movimento perpendicular
					if(posAtual == 7 && jogos.get(i).tabuleiro[2][1] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3   && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[2][0] = 'e';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][1] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[0][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 7 movimento diagonal
					if(posAtual == 7 && jogos.get(i).tabuleiro[0][1] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][0] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2 && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 2 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][1] = '.';
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 8 movimento perpendicular
					if(posAtual == 8 && jogos.get(i).tabuleiro[2][2] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							jogos.get(i).tabuleiro[2][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[2][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 3 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[2][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[2][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][2] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][2] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					//posAtual 8 movimento diagonal
					if(posAtual == 8 && jogos.get(i).tabuleiro[2][2] == 'e'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 0){
							jogos.get(i).tabuleiro[2][2] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1; //tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && jogos.get(i).tabuleiro[1][1] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[1][1] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 2 && orientacao == 0 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'E';//perpendicular
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 2 && orientacao == 1 && jogos.get(i).tabuleiro[0][0] == '.'){
							jogos.get(i).tabuleiro[2][2] = '.';
							jogos.get(i).tabuleiro[0][0] = 'e';//diagonal
							if(jogos.get(i).j2.vez == 0){
								jogos.get(i).j.vez = 0;
								jogos.get(i).j2.vez = 1;
								return 1;
							}
							jogos.get(i).j2.vez = 0;
							jogos.get(i).j.vez = 1;
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					
				}
			}
		}
		
		return -1;
	}

	@Override
	public String obtemOponente(int id) throws RemoteException {
		int idJog = idJogo;
		long tempoPassado = System.currentTimeMillis() - contTemBuscaJog;
		long segundos = tempoPassado /1000;
		System.out.println(segundos);
		if(segundos>=120){
			return "Tempo Esgotado";	
		}
		String n = "";
		char[][] tab = new char[3][3];
		for(int linha=0 ; linha<3 ; linha++){
			for(int coluna=0 ; coluna<3 ; coluna++){
				tab[linha][coluna]='.';
			}      
		}
		Jogador j1 = null;
		for(int h = 0; h<jogadores.size(); h++){
			if(jogadores.get(h).id == id){
				j1 = jogadores.get(h);
			}
		}
		//nao esquecer de mudar j1 esta numa partida para 1
		//Jogo jog = new Jogo(j1,tab);
		//jogos.add(jog);
		//for(int i = 0; i<jogadores.size(); i++){
		//	System.out.println("IdJogador:" + jogadores.get(i).id);
			if(j1.id == id && j1.estaNumPartida == 0){
				for(int l = 0; l<jogadores.size(); l++){
					if(jogadores.get(l).id != id && jogadores.get(l).estaNumPartida == 0){
						System.out.println("Id adversario: "+jogadores.get(l).id);
						Jogador j2 = jogadores.get(l);
						n = j2.nome;
						j2.estaNumPartida = 1;
						j1.estaNumPartida = 1;
						Jogo jog = new Jogo(j1,j2,tab);
						jog.numParticipantes = 2;
						jog.idJogo = idJog;
						jogos.add(jog);
						
						//jogos.get(l).idJogo = idJog;
						
						//System.out.println("IdJogo l: "+jogos.get(l).idJogo);
						//jogos.get(i).idJogo = idJog;
						//System.out.println("IdJogo i: "+jogos.get(i).idJogo);
						idJogo++;
						return "Seu oponente: "+n;
					}
				}
				
			}
		//}
		
		return "";//erro
	}
	
	public int verificaLinhas(int id){
		for(int i = 0; i < jogos.size(); i++){
			if(jogos.get(i).j.id == id || jogos.get(i).j2.id == id){
				 for(int linha=0 ; linha<3 ; linha++){
			            if( (jogos.get(i).tabuleiro[linha][0] == 'c'||jogos.get(i).tabuleiro[linha][0] == 'C') && (jogos.get(i).tabuleiro[linha][1] == 'c' || jogos.get(i).tabuleiro[linha][1] == 'C') &&  (jogos.get(i).tabuleiro[linha][2] == 'c'||jogos.get(i).tabuleiro[linha][2] == 'C')){
			                return 1;//claras
			            }
			            if( (jogos.get(i).tabuleiro[linha][0] == 'e'||jogos.get(i).tabuleiro[linha][0] == 'E') && (jogos.get(i).tabuleiro[linha][1] == 'e' || jogos.get(i).tabuleiro[linha][1] == 'E') &&  (jogos.get(i).tabuleiro[linha][2] == 'E'||jogos.get(i).tabuleiro[linha][2] == 'E')){
			            	return 2;//escuras
			            }       
			        }
			}
		}
       
        
        return 0;//ninguem completou todas as linhas                
    }
	
	  public int verificaColunas(int id){
		  for(int i = 0; i < jogos.size(); i++){
				if(jogos.get(i).j.id == id || jogos.get(i).j2.id == id){
					 for(int coluna=0 ; coluna<3 ; coluna++){
				        	if( (jogos.get(i).tabuleiro[0][coluna] == 'c'||jogos.get(i).tabuleiro[0][coluna] == 'C') && (jogos.get(i).tabuleiro[1][coluna] == 'c' || jogos.get(i).tabuleiro[1][coluna] == 'C') &&  (jogos.get(i).tabuleiro[2][coluna] == 'c'||jogos.get(i).tabuleiro[2][coluna] == 'C')){
				                return 1;//claras
				            }
				            if( (jogos.get(i).tabuleiro[0][coluna] == 'e'||jogos.get(i).tabuleiro[0][coluna] == 'E') && (jogos.get(i).tabuleiro[1][coluna] == 'e' || jogos.get(i).tabuleiro[1][coluna] == 'E') &&  (jogos.get(i).tabuleiro[2][coluna] == 'E'||jogos.get(i).tabuleiro[2][coluna] == 'E')){
				            	return 2;//escuras
				            }       
				        }
				}
		  }
	       
	        return 0;          
	 }
	  
	 public int verificaDiagonais(int id){
		 for(int i = 0; i < jogos.size(); i++){
				if(jogos.get(i).j.id == id || jogos.get(i).j2.id == id){
					if( (jogos.get(i).tabuleiro[0][0] == 'c'||jogos.get(i).tabuleiro[0][0] == 'C') && (jogos.get(i).tabuleiro[1][1] == 'c' || jogos.get(i).tabuleiro[1][1] == 'C') &&  (jogos.get(i).tabuleiro[2][2] == 'c'||jogos.get(i).tabuleiro[2][2] == 'C')){
						 return 1;//claras
					 }
					 if( (jogos.get(i).tabuleiro[0][0] == 'e'||jogos.get(i).tabuleiro[0][0] == 'E') && (jogos.get(i).tabuleiro[1][1] == 'e' || jogos.get(i).tabuleiro[1][1] == 'E') &&  (jogos.get(i).tabuleiro[2][2] == 'e'||jogos.get(i).tabuleiro[2][2] == 'E')){
						 return 2;//escuras
					 }
					 if( (jogos.get(i).tabuleiro[0][2] == 'c'||jogos.get(i).tabuleiro[0][0] == 'C') && (jogos.get(i).tabuleiro[1][1] == 'c' || jogos.get(i).tabuleiro[1][1] == 'C') &&  (jogos.get(i).tabuleiro[2][2] == 'c'||jogos.get(i).tabuleiro[2][0] == 'C')){
						 return 1;//claras
					 }
					 if( (jogos.get(i).tabuleiro[0][2] == 'e'||jogos.get(i).tabuleiro[0][0] == 'E') && (jogos.get(i).tabuleiro[1][1] == 'e' || jogos.get(i).tabuleiro[1][1] == 'E') &&  (jogos.get(i).tabuleiro[2][2] == 'e'||jogos.get(i).tabuleiro[2][0] == 'E')){
						 return 2;//escuras
					 }
				}
		 }
		 
		 return 0;
	 }
	
	 public int venceu(int id){
		 if(verificaLinhas(id) == 1){
			 return 1;//claras
		 }
		 if(verificaColunas(id) == 1){
			 return 1;//claras
		 }
		 if(verificaDiagonais(id) == 1){
			 return 1;//claras
		 }
		 if(verificaLinhas(id) == 2){
			 return 2;//escuras
		 }
		 if(verificaColunas(id) == 2){
			 return 2;//escuras
		 }
		 if(verificaDiagonais(id) == 2){
			 return 2;//escuras
		 }
		 return 0;//ninguem venceu ainda
	 }
	 
	 public boolean tabuleiroCheio(int id){
		 int cont = 0;
		 for(int i = 0; i < jogos.size(); i++){
				if(jogos.get(i).j.id == id){
					for(int linha=0 ; linha<3 ; linha++){
			        	for(int coluna=0 ; coluna<3 ; coluna++){
			        		if( jogos.get(i).tabuleiro[linha][coluna]!='.' ){
			        			 cont++;
			        		}
			        	}
			        } 
				}
		 }
	        
		 if(cont>=6){
	       	return true;
	     }
		 return false;
	 }

}
