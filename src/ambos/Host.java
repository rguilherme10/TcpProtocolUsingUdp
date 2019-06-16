package ambos;

import java.net.InetAddress;

public class Host {
	public InetAddress endereco;
	public int porta;
	public Host(String ip, int porta) throws Exception {
		// TODO Auto-generated constructor stub
		this.endereco = InetAddress.getByName(ip);
		this.porta=porta;
	}
	public Host(InetAddress endereco, int porta) {
		// TODO Auto-generated constructor stub
		this.endereco = endereco;
		this.porta=porta;
	}
}
