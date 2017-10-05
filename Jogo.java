import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

class Jogador{
	public String nome;
	public int id;
	public int numJogador;
	public int vez;
	public Jogador(String nome, int id, int numJogador, int vez){
		this.nome = nome;
		this.id = id;
		this.numJogador = numJogador;
		this.vez = vez;
	}
}

public class Jogo extends UnicastRemoteObject implements JogoInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6636057431278727855L;
	
	private ArrayList<Jogador> jogadores = new ArrayList<>();
	private ArrayList<Jogo> jogos = new ArrayList<>();
	private char[][] tabuleiro = new char[3][3];
	private char peca;
	private static int idJogador = 1;
	private static int idJogo = 1;
	private int numParticipantes;
	private Jogador j;
	
	public Jogo(int numParticipantes, Jogador j, int idJogo) throws RemoteException {
		this.numParticipantes = numParticipantes;
		this.j = j;
		this.idJogo = idJogo;
		for(int linha=0 ; linha<3 ; linha++){
			for(int coluna=0 ; coluna<3 ; coluna++){
				tabuleiro[linha][coluna]='.';
			}      
		}
            
	}
	
	
	@Override
	public int registraJogador(String nome) throws RemoteException {
		int id = idJogador;
		if(jogadores.size()>=2){
			return -2;//numero maximo de jogadores atingidos
		}
		for(int i = 0; i<jogadores.size(); i++){
			if(nome.equals(jogadores.get(i).nome)){
				return -1; //Usuariao ja cadastrado
			}
		}
		Jogador j = new Jogador(nome,id,0,0);
		jogadores.add(j);
		Jogo jog = new Jogo(1, j, 0);
		jogos.add(jog);
		idJogador++;
		return id;
	}

	@Override
	public int encerraPartida(int id) throws RemoteException {
		int acabou = ehMinhaVez(id);
		if(acabou !=1 && acabou != -1 && acabou != -2){
			for(int i = 0; i<jogos.size(); i++){
				if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes == 2){
					jogos.remove(i);
					return 1;
				}
			}		
		}
		
		return 0;//partida encerradad com sucesso
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
				
				for(int l = 0; l<jogos.size(); l++){
					//Para procurar pelo outro jogador que esta associado ao mesmo jogo pelo idJogo
					if(jogos.get(l).idJogo == jogos.get(i).idJogo && jogos.get(l).j.id != id){
						if(jogos.get(l).j.numJogador==1){
							jogos.get(i).j.numJogador = 2;
							return jogos.get(i).j.numJogador;
						}
						if(jogos.get(l).j.numJogador==2){
							jogos.get(i).j.numJogador = 1;
							jogos.get(i).j.vez = 1;
							return jogos.get(i).j.numJogador;
						}else{
							Random rand = new Random();
						    int randomNum = rand.nextInt((2 - 1) + 1) + 1;
						    jogos.get(i).j.numJogador = randomNum;
						    if(randomNum==1){
						    	jogos.get(i).j.vez = 1;
						    	jogos.get(l).j.numJogador = 2;
						    }
						    if(randomNum == 2){
						    	jogos.get(l).j.vez = 1;
						    	jogos.get(l).j.numJogador = 1;
						    }
							return randomNum;
						}
					}
				}	
			}
		}
		//placeholder, ainda tem que ser implementado
		int tempo = 1;
		if(tempo==0){
			return -2; //tempo de espera esgotado
		}else{
			return -1;//erro
		}
	}

	
	@Override
	public int ehMinhaVez(int id) throws RemoteException {
		int tempo1 = 1; //placeholder, para ser implementado timeout
		int tempo2 = 1; //placeholder, para ser implementado timeout
		for(int i = 0; i<jogos.size(); i++){
			if(jogos.get(i).j.id==id && jogos.get(i).numParticipantes !=2){
				return -2; //ainda nao tem 2 jogadores
			}
			if(jogos.get(i).j.id==id && venceu()!=0 && venceu()==jogos.get(i).j.numJogador){
				return 2; //eh o vencedor
			}
			if(jogos.get(i).j.id==id && venceu()!=0 && venceu()!=jogos.get(i).j.numJogador){
				return 3; //eh o perdedor
			}
			if(jogos.get(i).j.id == id && tempo1 == 0){ //placeholder
				return 6; //perdedor por WO
			}
			if(jogos.get(i).j.id == id && tempo2 == 0){ //placeholder
				return 5;//vencedor por WO
			}
			if(jogos.get(i).j.id == id && jogos.get(i).j.vez == 1){
				return 1;//sim
			}
			if(jogos.get(i).j.id == id && jogos.get(i).j.vez == 0){
				return 0;//nao
			}
			
		}
		return -1;//erro
	}

	@Override
	public String obtemTabuleiro(int id) throws RemoteException {
		
		String tab = "";
        for(int linha=0 ; linha<3 ; linha++){
        
            for(int coluna=0 ; coluna<3 ; coluna++){
                
                if(tabuleiro[linha][coluna]== '.'){
                    tab += " . ";
                }
                if(tabuleiro[linha][coluna]=='c'){
                    tab += " c ";
                }
                if(tabuleiro[linha][coluna]=='C'){
                    tab += " C ";
                }
                if(tabuleiro[linha][coluna]=='e'){
                    tab += " e ";
                }
                if(tabuleiro[linha][coluna]=='E'){
                    tab += " E ";
                }
                
                if(coluna==0 || coluna==1)
                    tab += "|";
            }
            tab += "\n";
        }
		return tab;
	}

	@Override
	public int posicionaPeca(int id, int pos, int orientacao) throws RemoteException {
		int vez = ehMinhaVez(id);
		if(vez == 1 && !tabuleiroCheio()){
			for(int i = 0; i<jogos.size(); i++){
				if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes!=2){
					return -2;//ainda nao tem dois jogadores na partida
				}
				if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes==2){
					//caso seja o jogador 1(claras)
					if(jogos.get(i).j.numJogador == 1){
						if(pos == 0 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[0][0] == '.'){
								tabuleiro[0][0] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 0 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[0][0] == '.'){
								tabuleiro[0][0] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[0][1] == '.'){
								tabuleiro[0][1] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[0][1] == '.'){
								tabuleiro[0][1] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[0][2] == '.'){
								tabuleiro[0][2] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[0][2] == '.'){
								tabuleiro[0][2] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[1][0] == '.'){
								tabuleiro[1][0] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[1][0] == '.'){
								tabuleiro[1][0] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[1][1] == '.'){
								tabuleiro[1][1] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[1][1] == '.'){
								tabuleiro[1][1] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[1][2] == '.'){
								tabuleiro[1][2] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[1][2] == '.'){
								tabuleiro[1][2] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[2][0] == '.'){
								tabuleiro[2][0] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[2][0] == '.'){
								tabuleiro[2][0] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[2][1] == '.'){
								tabuleiro[2][1] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[2][1] == '.'){
								tabuleiro[2][1] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh C
							if(tabuleiro[2][2] == '.'){
								tabuleiro[2][2] = 'C';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh c
							if(tabuleiro[2][2] == '.'){
								tabuleiro[2][2] = 'c';
								return 1;
							}else{
								return -1;
							}
						}
						
					}
					//caso seja o jogador 2(escuras)
					if(jogos.get(i).j.numJogador == 2){
						if(pos == 0 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[0][0] == '.'){
								tabuleiro[0][0] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 0 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[0][0] == '.'){
								tabuleiro[0][0] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[0][1] == '.'){
								tabuleiro[0][1] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 1 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[0][1] == '.'){
								tabuleiro[0][1] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[0][2] == '.'){
								tabuleiro[0][2] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 2 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[0][2] == '.'){
								tabuleiro[0][2] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[1][0] == '.'){
								tabuleiro[1][0] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 3 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[1][0] == '.'){
								tabuleiro[1][0] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[1][1] == '.'){
								tabuleiro[1][1] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 4 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[1][1] == '.'){
								tabuleiro[1][1] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[1][2] == '.'){
								tabuleiro[1][2] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 5 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[1][2] == '.'){
								tabuleiro[1][2] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[2][0] == '.'){
								tabuleiro[2][0] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 6 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[2][0] == '.'){
								tabuleiro[2][0] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[2][1] == '.'){
								tabuleiro[2][1] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 7 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[2][1] == '.'){
								tabuleiro[2][1] = 'e';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 0){ //orientacao = 0 eh perpendicular, que eh E
							if(tabuleiro[2][2] == '.'){
								tabuleiro[2][2] = 'E';
								return 1;
							}else{
								return -1;
							}
						}
						if(pos == 8 && orientacao == 1){ //orientacao = 1 eh diagonal, que eh e
							if(tabuleiro[2][2] == '.'){
								tabuleiro[2][2] = 'e';
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
		if(tabuleiroCheio()){
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
		if(!tabuleiroCheio()){
			return -4; //tabuleiro nao esta cheio
		}
		
		for(int i = 0; i<jogos.size(); i++){
			if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes !=2){
				return -2;//partida nao inicada ainda, nao ha dois jogadores
			}
			if(jogos.get(i).j.id == id && jogos.get(i).numParticipantes == 2){
				//claras
				if(jogos.get(i).j.numJogador == 1){
					if(posAtual == 0 && tabuleiro[0][0] == 'C'  ){
						if(sentidoDesl == 0 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[0][0] = 'c';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][0] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 0 && tabuleiro[0][0] == 'c'  ){
						if(sentidoDesl == 0 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[0][0] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 1 && tabuleiro[0][1] == 'C'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[0][1] = 'c';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 2 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 0   && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][0] = 'c';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[2][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[2][1] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 1 && tabuleiro[0][1] == 'c'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[0][1] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 2 && tabuleiro[0][2] == 'C'  ){
						if(sentidoDesl == 2 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[0][2] = 'c';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 2 && tabuleiro[0][2] == 'c'  ){
						if(sentidoDesl == 2 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[0][2] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][0] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 3 && tabuleiro[1][0] == 'C'  ){
						if(sentidoDesl == 3 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[1][0] = 'c';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 6 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 3 && tabuleiro[1][0] == 'c'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[1][0] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][1] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 4 && tabuleiro[1][1] == 'C'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[1][1] = 'c';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 4 && tabuleiro[1][1] == 'c'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[1][1] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][0] = 'c';//diagonal
							return 1;//tudo certo
						}						
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 5 && tabuleiro[1][2] == 'C'  ){
						if(sentidoDesl == 5 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[1][2] = 'c';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 5 && tabuleiro[1][2] == 'c'  ){
						if(sentidoDesl == 5 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[1][2] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][1] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 6 && tabuleiro[2][0] == 'C'  ){
						if(sentidoDesl == 6 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[2][0] = 'c';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][0] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 6 && tabuleiro[2][0] == 'c'  ){
						if(sentidoDesl == 6 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[2][0] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 7 && tabuleiro[2][1] == 'C'  ){
						if(sentidoDesl == 7 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[2][1] = 'c';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 6   && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][0] = 'c';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[0][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[0][1] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 7 && tabuleiro[0][1] == 'c'  ){
						if(sentidoDesl == 7 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[2][1] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 8 && tabuleiro[2][2] == 'C'  ){
						if(sentidoDesl == 8 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[2][2] = 'c';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][0] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][2] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][2] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][2] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 8 && tabuleiro[2][2] == 'c'  ){
						if(sentidoDesl == 2 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[2][2] = 'C';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][1] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][1] = 'c';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][0] = 'C';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][0] = 'c';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
				}
				//escuras
				if(jogos.get(i).j.numJogador == 1){
					if(posAtual == 0 && tabuleiro[0][0] == 'E'  ){
						if(sentidoDesl == 0 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[0][0] = 'e';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[0][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][0] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 0 && tabuleiro[0][0] == 'e'  ){
						if(sentidoDesl == 0 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[0][0] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[0][0] = '.';
							tabuleiro[2][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 1 && tabuleiro[0][1] == 'E'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[0][1] = 'e';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 2 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 0   && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[0][0] = 'e';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[2][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[2][1] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 1 && tabuleiro[0][1] == 'e'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[0][1] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[0][1] = '.';
							tabuleiro[1][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 2 && tabuleiro[0][2] == 'E'  ){
						if(sentidoDesl == 2 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[0][2] = 'e';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 1 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[0][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 2 && tabuleiro[0][2] == 'e'  ){
						if(sentidoDesl == 2 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[0][2] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 6 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[0][2] = '.';
							tabuleiro[2][0] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 3 && tabuleiro[1][0] == 'E'  ){
						if(sentidoDesl == 3 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[1][0] = 'e';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 6 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 5 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[1][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 3 && tabuleiro[1][0] == 'e'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[1][0] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[0][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 7 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[1][0] = '.';
							tabuleiro[2][1] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 4 && tabuleiro[1][1] == 'E'  ){
						if(sentidoDesl == 4 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[1][1] = 'e';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 1 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 5  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[1][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 4 && tabuleiro[1][1] == 'e'  ){
						if(sentidoDesl == 1 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[1][1] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[2][0] = 'e';//diagonal
							return 1;//tudo certo
						}						
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 0  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[1][1] = '.';
							tabuleiro[0][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 5 && tabuleiro[1][2] == 'E'  ){
						if(sentidoDesl == 5 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[1][2] = 'e';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 3 || sentidoDesl == 4 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[1][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 2  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 5 && tabuleiro[1][2] == 'e'  ){
						if(sentidoDesl == 5 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[1][2] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 1  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[0][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 7  && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[1][2] = '.';
							tabuleiro[2][1] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 6 && tabuleiro[2][0] == 'E'  ){
						if(sentidoDesl == 6 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[2][0] = 'e';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 7 || sentidoDesl == 8 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[2][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 0 || sentidoDesl == 3 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][0] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 6 && tabuleiro[2][0] == 'e'  ){
						if(sentidoDesl == 6 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[2][0] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[2][0] = '.';
							tabuleiro[0][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 7 && tabuleiro[2][1] == 'E'  ){
						if(sentidoDesl == 7 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[2][1] = 'e';//diagonal
							return 1; //tudo certo
						}
						if(sentidoDesl == 8 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 8  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 6  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 6   && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[2][0] = 'e';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[0][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 1 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][1] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[0][1] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 7 && tabuleiro[0][1] == 'e'  ){
						if(sentidoDesl == 7 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[2][1] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 3  && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][0] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if(sentidoDesl == 5 && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[2][1] = '.';
							tabuleiro[1][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 8 && tabuleiro[2][2] == 'E'  ){
						if(sentidoDesl == 8 && casasDeslocadas == 0 && orientacao == 1){
							tabuleiro[2][2] = 'e';//diagonal
							return 1; //tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[2][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[2][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[2][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 6 || sentidoDesl == 7 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[2][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[2][0] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][2] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][2] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 5 || sentidoDesl == 2 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][2] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][2] = 'e';//diagonal
							return 1;//tudo certo
						}else{
							return 0;
						}	
					}
					if(posAtual == 8 && tabuleiro[2][2] == 'e'  ){
						if(sentidoDesl == 2 && casasDeslocadas == 0 && orientacao == 0){
							tabuleiro[2][2] = 'E';//perpendicular
							return 1; //tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 0 && tabuleiro[1][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][1] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 1 && orientacao == 1 && tabuleiro[1][1] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[1][1] = 'e';//diagonal
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 0 && tabuleiro[0][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][0] = 'E';//perpendicular
							return 1;//tudo certo
						}
						if((sentidoDesl == 4 || sentidoDesl == 0 ) && casasDeslocadas == 2 && orientacao == 1 && tabuleiro[0][0] == '.'){
							tabuleiro[2][2] = '.';
							tabuleiro[0][0] = 'e';//diagonal
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
		for(int i = 0; i<jogos.size(); i++){
			if(jogos.get(i).idJogo == 0){
				for(int l = 0; l<jogos.size(); l++){
					if(jogos.get(l).j.id == id){
						jogos.get(l).numParticipantes = 2;
						jogos.get(l).idJogo = idJog;
					}
				}
				jogos.get(i).numParticipantes = 2;
				jogos.get(i).idJogo = idJog;
				idJogo++;
				return "Seu oponente: "+jogos.get(i).j.nome;
			}
		}
		
		return "";//erro
	}
	
	public int verificaLinhas(){
        for(int linha=0 ; linha<3 ; linha++){
            if( (tabuleiro[linha][0] == 'c'||tabuleiro[linha][0] == 'C') && (tabuleiro[linha][1] == 'c' || tabuleiro[linha][1] == 'C') &&  (tabuleiro[linha][2] == 'c'||tabuleiro[linha][2] == 'C')){
                return 1;//claras
            }
            if( (tabuleiro[linha][0] == 'e'||tabuleiro[linha][0] == 'E') && (tabuleiro[linha][1] == 'e' || tabuleiro[linha][1] == 'E') &&  (tabuleiro[linha][2] == 'E'||tabuleiro[linha][2] == 'E')){
            	return 2;//escuras
            }       
        }
        
        return 0;//ninguem completou todas as linhas                
    }
	
	  public int verificaColunas(){
	        for(int coluna=0 ; coluna<3 ; coluna++){
	        	if( (tabuleiro[0][coluna] == 'c'||tabuleiro[0][coluna] == 'C') && (tabuleiro[1][coluna] == 'c' || tabuleiro[1][coluna] == 'C') &&  (tabuleiro[2][coluna] == 'c'||tabuleiro[2][coluna] == 'C')){
	                return 1;//claras
	            }
	            if( (tabuleiro[0][coluna] == 'e'||tabuleiro[0][coluna] == 'E') && (tabuleiro[1][coluna] == 'e' || tabuleiro[1][coluna] == 'E') &&  (tabuleiro[2][coluna] == 'E'||tabuleiro[2][coluna] == 'E')){
	            	return 2;//escuras
	            }       
	        }
	        return 0;          
	 }
	  
	 public int verificaDiagonais(){
		 if( (tabuleiro[0][0] == 'c'||tabuleiro[0][0] == 'C') && (tabuleiro[1][1] == 'c' || tabuleiro[1][1] == 'C') &&  (tabuleiro[2][2] == 'c'||tabuleiro[2][2] == 'C')){
			 return 1;//claras
		 }
		 if( (tabuleiro[0][0] == 'e'||tabuleiro[0][0] == 'E') && (tabuleiro[1][1] == 'e' || tabuleiro[1][1] == 'E') &&  (tabuleiro[2][2] == 'e'||tabuleiro[2][2] == 'E')){
			 return 2;//escuras
		 }
		 if( (tabuleiro[0][2] == 'c'||tabuleiro[0][0] == 'C') && (tabuleiro[1][1] == 'c' || tabuleiro[1][1] == 'C') &&  (tabuleiro[2][2] == 'c'||tabuleiro[2][0] == 'C')){
			 return 1;//claras
		 }
		 if( (tabuleiro[0][2] == 'e'||tabuleiro[0][0] == 'E') && (tabuleiro[1][1] == 'e' || tabuleiro[1][1] == 'E') &&  (tabuleiro[2][2] == 'e'||tabuleiro[2][0] == 'E')){
			 return 2;//escuras
		 }
		 
		 return 0;
	 }
	
	 public int venceu(){
		 if(verificaLinhas() == 1){
			 return 1;//claras
		 }
		 if(verificaColunas() == 1){
			 return 1;//claras
		 }
		 if(verificaDiagonais() == 1){
			 return 1;//claras
		 }
		 if(verificaLinhas() == 2){
			 return 2;//escuras
		 }
		 if(verificaColunas() == 2){
			 return 2;//escuras
		 }
		 if(verificaDiagonais() == 2){
			 return 2;//escuras
		 }
		 return 0;//ninguem venceu ainda
	 }
	 
	 public boolean tabuleiroCheio(){
	        for(int linha=0 ; linha<3 ; linha++){
	        	for(int coluna=0 ; coluna<3 ; coluna++){
	        		if( tabuleiro[linha][coluna]=='.' ){
	        			 return false;
	        		}
	        	}
	        }        
	        return true;
	 }

}
