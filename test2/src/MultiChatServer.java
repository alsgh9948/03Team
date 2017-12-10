
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

//Ŭ���̾�Ʈ�� ���� ���۵� ���ڿ��� �޾Ƽ� �ٸ� Ŭ���̾�Ʈ���� ���ڿ���
//�����ִ� ������
class newFileThread extends Thread
{
	Socket filesocket = null;
	ServerSocket server = null;
	public newFileThread()
	{
		try {
			server = new ServerSocket(3001);
		}
		catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
	public void run()
	{
		try{
			while(true){
				filesocket = server.accept();	
				if(filesocket!=null)
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(
							filesocket.getInputStream()));
					String imageFileName = br.readLine();
					File createdFile = new File("C:\\Users\\seo\\Desktop\\a\\", imageFileName);

					ObjectInputStream ois = new ObjectInputStream(filesocket.getInputStream());
					BufferedImage bi = ImageIO.read(ois);

					br.close();
					ois.close();

					ImageIO.write(bi, "jpg", createdFile);
					System.out.println("THE END --- SERVER");
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
class EchoThread extends Thread{
	Socket socket;
	Vector<Socket> vec;
	public EchoThread(Socket socket, Vector<Socket> vec){
		this.socket = socket;
		this.vec = vec;
	}
	public void run(){
		BufferedReader br = null;
		try{
			br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String str =null;
			while(true){
				//Ŭ���̾�Ʈ�� ���� ���ڿ� �ޱ�
				str=br.readLine();
				//��밡 ������ ������ break;
				if(str==null){
					//���Ϳ��� ���ֱ�
					vec.remove(socket);
					break;
				}
				//����� ���ϵ��� ���ؼ� �ٸ� Ŭ���̾�Ʈ���� ���ڿ� �����ֱ�
				sendMsg(str);				
			}
			
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(br != null) br.close();
				if(socket != null) socket.close();
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}
	
	//���۹��� ���ڿ� �ٸ� Ŭ���̾�Ʈ�鿡�� �����ִ� �޼���
	public void sendMsg(String str){
		try{
			for(Socket socket:vec){
				//for�� ���� ������ socket�� �����͸� ���� Ŭ���̾�Ʈ�� ��츦 �����ϰ� 
				//������ socket�鿡�Ը� �����͸� ������.
				if(socket != this.socket){
					PrintWriter pw = 
						new PrintWriter(socket.getOutputStream(), true);
					pw.println(str);
					pw.flush();
					//��,���⼭ ���� ���ϵ��� ���ǰ͵��̱� ������ ���⼭ ������ �ȵȴ�.
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}

class server_frame extends JFrame implements ActionListener
{
	DB db = new DB();
	JButton btn = new JButton("��������");
	
	public server_frame()
	{
		super("����");
		
		JPanel p = new JPanel();
		p.add(btn);
		
		add(p);
		btn.addActionListener(this);
		db.delete_data();
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		
		setBounds(2, 2, 150, 100);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == btn)
		{
			db.delete_data();
			System.exit(0);
		}
		
	}
}
public class MultiChatServer {
	public static void main(String[] args) {
		ServerSocket server = null;
		Socket socket =null;

		server_frame f = new server_frame();
		//Ŭ���̾�Ʈ�� ����� ���ϵ��� �迭ó�� ������ ���Ͱ�ü ����
		Vector<Socket> vec = new Vector<Socket>();
		try{
			server= new ServerSocket(3000);
			while(true){
				System.out.println("���Ӵ����..");
				
				socket = server.accept();
				
				//Ŭ���̾�Ʈ�� ����� ������ ���Ϳ� ���
				vec.add(socket);
				//������ ����
				new newFileThread().start();
				new EchoThread(socket, vec).start();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
