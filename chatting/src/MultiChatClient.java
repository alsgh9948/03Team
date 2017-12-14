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
		//키보드로부터 읽어오기 위한 스트림객체 생성

		PrintWriter pw=null;
		String msg;
		try{
			//서버로 문자열 전송하기 위한 스트림객체 생성
			pw=new PrintWriter(socket.getOutputStream(),true);
			if(cf.isFirst==true){
				InetAddress iaddr=socket.getLocalAddress();				
				String ip = iaddr.getHostAddress();				
				getId();
				System.out.println("ip:"+ip+"id:"+id);
				str = "["+id+"] 님 로그인 ("+ip+")"; 
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
			//입력받은 문자열 서버로 보내기
			pw.println(str);

		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}	
	public void getId(){		
		id = Id.getId(); 
	}
}
//서버가 보내온 문자열을 전송받는 스레드
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
			//서버로부터 전송된 문자열 읽어오기 위한 스트림객체 생성
			br=new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while(true){
				//소켓으로부터 문자열 읽어옴
				String str=br.readLine();
				StringTokenizer token_str = new StringTokenizer(str,"((@!");
					if(str.contains("((@!"))
					{
						Imageprint i_p = new Imageprint("image\\"+token_str.nextToken());
						MultiChatClient.filecheck = false;
					}
					else cf.txtA.append(str+"\n");
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
	JButton btn = new JButton("서버를 먼저 켜주세요");

	public server_error()
	{
		super("서버 에러");

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
			System.out.println("연결성공!");
			cf = new ClientFrame(socket);
			new ReadThread(socket, cf).start();
		}catch(IOException ie){
			server_error Error = new server_error(); 
		}
	}
}