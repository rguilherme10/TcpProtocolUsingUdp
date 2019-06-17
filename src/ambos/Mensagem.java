package ambos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Mensagem implements Comparable<Mensagem> {
	public Host host;
	private Pacote pacote;
	public boolean enviada = false;
	public boolean deuTimeout = false;
	public boolean retornouAck = false;
	
	public Mensagem(Host host, Pacote pacoteTCP) {
		this.host=host;
		this.pacote=pacoteTCP;
	}
	
	public int getPortaOrigem() {
		return pacote.segmentoTCP.getPortaOrigem();
	}
	
	public int getNumeroSequencia() {
		return pacote.segmentoTCP.getNumeroSequencia();
	}

	public int getNumeroACK() {
		return pacote.segmentoTCP.getNumeroACK();
	}

	public int getTamanhoJanelaReceptor() {
		return pacote.segmentoTCP.getTamanhoJanelaReceptor();
	}

	public byte[] getDados() {
		return pacote.segmentoTCP.dados;
	}

	public boolean isSyn() {
		try {
			return (pacote.segmentoTCP.syn==1 && pacote.segmentoTCP.ack==0 && pacote.segmentoTCP.fin==0);
		}
		catch(Exception e) {}
		return false;
	}
	public boolean isSynAck() {
		return (pacote.segmentoTCP.syn==1 && pacote.segmentoTCP.ack==1 && pacote.segmentoTCP.fin==0);
	}
	public boolean isAck() {
		return (pacote.segmentoTCP.syn==0 && pacote.segmentoTCP.ack==1 && pacote.segmentoTCP.fin==0);
	}
	public boolean isFin() {
		return (pacote.segmentoTCP.syn==0 && pacote.segmentoTCP.ack==0 && pacote.segmentoTCP.fin==1);
	}
	public boolean isFinAck() {
		return (pacote.segmentoTCP.syn==0 && pacote.segmentoTCP.ack==1 && pacote.segmentoTCP.fin==1);
	}

	public boolean isControle() {
		try {
			return (pacote.segmentoTCP.syn==1 || pacote.segmentoTCP.ack==1 || pacote.segmentoTCP.fin==1);
		}
		catch(Exception e) {}
		return false;
	}
	
	public int size() {
		if(this.isControle()) {
			return 21;
		}
		else {
			return 21 + pacote.size();
		}
	}
	
	public static Mensagem receberMensagem(int porta, int tcp_maximum_segment_size) {
		Mensagem retorno=null;
		DatagramSocket serverSocket=null;
		try {
			serverSocket = new DatagramSocket(porta);
			byte[] receiveData = new byte[21 + tcp_maximum_segment_size];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					21 + tcp_maximum_segment_size);
			serverSocket.receive(receivePacket);
			byte[] pacote = new byte[21];
			System.arraycopy(receivePacket.getData(), 0, pacote, 0, pacote.length);
			Pacote pacoteTCP = new Pacote(pacote);
			if(pacoteTCP.size()>0) {
				pacoteTCP.segmentoTCP.dados = new byte[pacoteTCP.size()];
				System.arraycopy(receivePacket.getData(), pacoteTCP.headerSize(), pacoteTCP.segmentoTCP.dados, 0, pacoteTCP.segmentoTCP.dados.length);
			}
			Host host = new Host(receivePacket.getAddress(), receivePacket.getPort());
			retorno = new Mensagem(host, pacoteTCP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(serverSocket!=null) {
				serverSocket.close();
			}
		}
		return retorno;
	}
	
	public void enviarMensagem(double probabilidadeDescarte, int timeout){
		
		deuTimeout = false;
		if(Math.random() > probabilidadeDescarte) {
			DatagramSocket datagramSocket=null;
			try {
				datagramSocket = new DatagramSocket();
				DatagramPacket datagramPacket = new DatagramPacket(pacote.toBytes(),
						pacote.headerSize() + pacote.size(), host.endereco, host.porta);
				datagramSocket.send(datagramPacket);
			}catch (Exception e) {
				e.printStackTrace();
			} // Ignora se não enviar mensagem
			finally {
				if(datagramSocket!=null) {
					datagramSocket.close();
				}
			}
		}
		this.enviada=true;
		if(timeout>0) {
			final Timer timerTimeout = new Timer("timerTimeout"+getNumeroSequencia());
			timerTimeout.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					deuTimeout = true;
					try {
						timerTimeout.cancel();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, timeout);
		}
	}

	@Override
	public int compareTo(Mensagem o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCalculaACK() {
		return this.getNumeroSequencia() + 1;
	}
}
