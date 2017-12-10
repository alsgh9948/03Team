import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.StringTokenizer;
// Ű����� ���۹��ڿ� �Է¹޾� ������ �����ϴ� ������
class WriteThread{

	Socket socket;
	ClientFrame cf;
	String str;
	String id;

	DB db = new DB();
	public WriteThread(ClientFrame cf) {
		this.cf  = cf;
		this.socket= cf.socket;
	}
	public void sendMsg(String a) {
		//Ű����κ��� �о���� ���� ��Ʈ����ü ����
		BufferedReader br=
				new BufferedReader(new InputStreamReader(System.in));
		PrintWriter pw=null;
		String msg;
		try{
			//������ ���ڿ� �����ϱ� ���� ��Ʈ����ü ����
			pw=new PrintWriter(socket.getOutputStream(),true);
			//ù��° �����ʹ� id �̴�. ���濡�� id�� �Բ� �� IP�� �����Ѵ�.
			if(cf.isFirst==true){
				InetAddress iaddr=socket.getLocalAddress();				
				String ip = iaddr.getHostAddress();				
				getId();
				System.out.println("ip:"+ip+"id:"+id);
				str = "["+id+"] �� �α��� ("+ip+")"; 
			}else{
				if(a !=null)
				{
					str = a;
					MultiChatClient.filecheck = true;
				}
				else{
					msg = cf.txtF.getText();
					str= "["+id+"] "+msg;
					db.DB_input(msg);
					}
			}
			//�Է¹��� ���ڿ� ������ ������
			pw.println(str);

		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(br!=null) br.close();
				//if(pw!=null) pw.close();
				//if(socket!=null) socket.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}	
	public void getId(){		
		id = Id.getId(); 
	}
}
//������ ������ ���ڿ��� ���۹޴� ������
class ReadThread extends Thread{
	Socket socket;
	ClientFrame cf;
	public ReadThread(Socket socket, ClientFrame cf) {
		this.cf = cf;
		this.socket=socket;
	}
	public void run() {
		BufferedReader br=null;
		try{
			//�����κ��� ���۵� ���ڿ� �о���� ���� ��Ʈ����ü ����
			br=new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while(true){
				//�������κ��� ���ڿ� �о��
				String str=br.readLine();
				StringTokenizer token_str = new StringTokenizer(str,"((@!");
				
				if(str==null){
					System.out.println("������ ������");
					break;
				}
				else
				{
					if(str.contains("((@!"))
					{
						Imageprint i_p = new Imageprint(token_str.nextToken());
						MultiChatClient.filecheck = false;
					}
					//���۹��� ���ڿ� ȭ�鿡 ���
					//System.out.println("[server] " + str);
					else cf.txtA.append(str+"\n");
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(br!=null) br.close();
				if(socket!=null) socket.close();
			}catch(IOException ie){}
		}
	}
}
class server_error extends JFrame implements ActionListener
{
	JButton btn = new JButton("������ ���� ���ּ���");

	public server_error()
	{
		super("���� ����");

		JPanel p = new JPanel();
		p.add(btn);

		add(p);
		btn.addActionListener(this);
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);

		setBounds(2, 100, 250, 80);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btn)
		{
			System.exit(0);
		}

	}
}
public class MultiChatClient {
	public static boolean filecheck = false;

	public static void main(String[] args) {
		Socket socket=null;
		ClientFrame cf;
		try{
			socket=new Socket("127.0.0.1",3000);
			System.out.println("���Ἲ��!");
			cf = new ClientFrame(socket);
			new ReadThread(socket, cf).start();
		}catch(IOException ie){
			server_error Error = new server_error(); 
		}
	}
}