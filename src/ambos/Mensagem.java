package ambos;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Mensagem implements Comparable<Mensagem> {
	public Host host;
	//public int numero_de_sequencia;
	public DadosTCP pacoteTCP;
	public boolean enviada = false;
	public int timeout = 0;
	public boolean retornouAck = false;
	public Timer timer = new Timer();
	
	public Mensagem(Host host, DadosTCP pacoteTCP, int timeout) {
		// TODO Auto-generated constructor stub
		this.host=host;
		//this.numero_de_sequencia = numero_de_sequencia;
		this.pacoteTCP=pacoteTCP;
		this.timeout=timeout;
	}
	
	public static Mensagem receberMensagem(int porta, int tcp_maximum_segment_size) {
		Mensagem retorno=null;
		try {
			DatagramSocket serverSocket = new DatagramSocket(porta);
			byte[] receiveData = new byte[tcp_maximum_segment_size];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			DadosTCP pacoteTCP = new DadosTCP(receivePacket.getData());
			Host host = new Host(receivePacket.getAddress(), receivePacket.getPort());
			retorno = new Mensagem(host, pacoteTCP, 0);
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retorno;
	}
	
	public void enviarMensagem(double probabilidadeDescarte){
		if(Math.random() > probabilidadeDescarte) {
			try {
				DatagramSocket datagramSocket = new DatagramSocket();
				DatagramPacket datagramPacket = new DatagramPacket(pacoteTCP.toBytes(),
						17 + pacoteTCP.size(),
						host.endereco, host.porta);
				datagramSocket.send(datagramPacket);
				datagramSocket.close();	
			}catch (Exception e) {
				e.printStackTrace();
			} // Ignora se não enviar mensagem
		}
		enviada = true;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				timeout = 0;
				try {
					timer.cancel();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, timeout);
	}

	@Override
	public int compareTo(Mensagem o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
