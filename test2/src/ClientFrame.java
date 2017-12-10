
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JDialog;

class FileThread extends Thread{
	Socket socket;
	Vector<Socket> vec;
	public FileThread(Socket socket, Vector<Socket> vec){
		this.socket = socket;
		this.vec = vec;
	}
	public void run(){
		BufferedInputStream bin=null;

		try{
			bin = new BufferedInputStream(socket.getInputStream());
			String str =null;

			String sendString= "";
			int n=0;
			while(true){
				//클라이언트로 부터 문자열 받기								
				byte b[] = new byte[255];
				n=bin.read(b);				

				//상대가 접속을 끊으면 break;
				if(n==-1){
					//벡터에서 없애기
					vec.remove(socket);
					break;
				}
				System.out.println("파일서버의 sendMsg호출");
				//연결된 소켓들을 통해서 다른 클라이언트에게 문자열 보내주기
				sendMsg(b);								
			}

		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(bin != null) bin.close();
				if(socket != null) {
					System.out.println("socketclose 호출");
					socket.close();
				}
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}

	//전송받은 문자열 다른 클라이언트들에게 보내주는 메서드
	public void sendMsg(byte b[]){
		//		String str1 = new String(b);
		//		System.out.println(str1);
		try{
			for(Socket socket:vec){
				//for를 돌되 현재의 socket이 데이터를 보낸 클라이언트인 경우를 제외하고 
				//나머지 socket들에게만 데이터를 보낸다.
				if(socket != this.socket){
					BufferedOutputStream bout  = new BufferedOutputStream(
							socket.getOutputStream());
					bout.write(b);
					bout.flush();
					//단,여기서 얻어온 소켓들은 남의것들이기 때문에 여기서 닫으면 안된다.
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
class Id extends JFrame implements ActionListener{
	static JTextField tf=new JTextField(8);
	JButton btn = new JButton("입력");	
	WriteThread wt;	
	ClientFrame cf;
	public Id(){}
	public Id(final WriteThread wt, final ClientFrame cf) {
		super("채팅접속");		
		this.wt = wt;
		this.cf = cf;
		setLayout(new FlowLayout());
		add(new JLabel("아이디"));
		add(tf);
		add(btn);

		//엔터키 전송
		tf.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e) {
				try{
					if(e.getKeyCode()==10){
						wt.sendMsg(null);	
						cf.isFirst = false;
						cf.setVisible(true);
						dispose();
					}
				}catch (Exception ee) {} 
			}
		});
		btn.addActionListener(this);


		setBounds(300, 300, 250, 100);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {		
		wt.sendMsg(null);	
		cf.isFirst = false;
		cf.setVisible(true);
		this.dispose();
	}
	static public String getId(){
		return tf.getText();
	}
}

public class ClientFrame extends JFrame implements ActionListener{

	JTextArea txtA = new JTextArea();
	JTextField txtF = new JTextField(15);
	JScrollPane scroll = new JScrollPane(txtA);
	JButton btnTransfer = new JButton("전송");
	JButton btemt = new JButton("이모티콘"); //이모티콘 
	JButton btnExit = new JButton("닫기");
	JButton btnKeyword = new JButton("키워드");

	String file_name;
	boolean filecheck = true;
	boolean isFirst=true;
	JPanel p1 = new JPanel();
	Socket socket;
	WriteThread wt;

	public ClientFrame(Socket socket) {
		super("03팀 채팅");
		this.socket = socket;
		wt = new WriteThread(this);
		new Id(wt, this);

		add("Center", scroll);

		txtA.setBackground(Color.yellow);
		add("Center", txtA);

		p1.add(btemt);
		p1.add(txtF);
		p1.add(btnTransfer);
		p1.add(btnKeyword);
		p1.add(btnExit);
		add("South", p1);

		//엔터키 전송
		txtF.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e) {
				String id = Id.getId();
				try{
					if(e.getKeyCode()==10){
						txtA.append("["+id+"] "+ txtF.getText()+"\n");
						wt.sendMsg(null);
						txtF.setText("");
					}
				}catch (Exception ee) {} 
			}
		});

		btnTransfer.addActionListener(this);
		btemt.addActionListener(this);
		btnKeyword.addActionListener(this);
		btnExit.addActionListener(this); //new PaintPanel() < 고침 닫기가 실행되지않음
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		setBounds(300, 300, 500, 400);
		setVisible(false);	
	}

	public void actionPerformed(ActionEvent e){
		String id = Id.getId();
		if(e.getSource()==btnTransfer){//전송버튼 눌렀을 경우
			//메세지 입력없이 전송버튼만 눌렀을 경우
			if(txtF.getText().equals("")){
				return;
			}			
			txtA.append("["+id+"] "+ txtF.getText()+"\n");
			wt.sendMsg(null);
			txtF.setText("");
		}else if(e.getSource()==btemt) //이모티콘 클릭시
		{
			paintFrame p_f = new paintFrame();
		}
		else if(e.getSource() == btnKeyword)
		{
			String[] Keyword = new String[3];
			String[] count = new String[3];
			DB db = new DB();
			db.print_data(Keyword,count);
			KeywordFrame keywordFrame = null;
			if(Keyword[0] == null || Keyword[1] == null || Keyword[2] == null)
				keywordFrame = new KeywordFrame();
			else keywordFrame = new KeywordFrame(Keyword,count);
		}
		else {
			this.dispose();
		}
	}
	public class paintFrame extends JFrame implements ActionListener{
		JButton makebtn = new JButton("만들기");
		JButton loadbtn = new JButton("보내기");
		JLabel Label1 = new JLabel("파일 경로 : 바탕화면 image 폴더");
		JLabel Label2 = new JLabel("파일 이름 : image.png");
		JPanel p = new JPanel();
		paintFrame()
		{
			super("이모티콘");
			p.add(makebtn);
			p.add(loadbtn);
			p.add(Label1);
			p.add(Label2);
			add(p);

			makebtn.addActionListener(this);
			loadbtn.addActionListener(this);
			setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
			this.setBounds(900, 300, 300, 150);
			this.setVisible(true);
		}
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==makebtn){
				Runtime run = Runtime.getRuntime();
				try {
					run.exec("C:\\WINDOWS\\system32\\mspaint.exe");
					paintFrame p = new paintFrame();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(e.getSource()==loadbtn) //보내기
			{
				try {
					file_name = "Winter.jpg";
					filecheck = false;
					File imageFile = new File(file_name);
					Socket clientSocket = new Socket("127.0.0.1", 3001);

					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							clientSocket.getOutputStream()));
					bw.write(imageFile.getName() + "\n");
					bw.flush();

					BufferedImage bi = ImageIO.read(imageFile);
					ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
					ImageIO.write(bi, "jpg", oos);

					clientSocket.close();
					bw.close();
					oos.close();
					
					String id = Id.getId();
					txtA.append("["+id+"] "+"을 전송하였습니다.\n");
					wt.sendMsg(file_name+"((@!");//9921
					
					System.out.println("THE END --- CLIENT");
					//Imageprint i = new Imageprint();
				}
				catch(Exception e1)
				{
					System.out.println(e1.getMessage());
				}
			}
		}
	}
	public class KeywordFrame extends JFrame{
		JLabel KeywordLabel1 = null;
		JLabel KeywordLabel2 = null;
		JLabel KeywordLabel3 = null;
		JLabel term1 = new JLabel("                                    ");
		JLabel term2 = new JLabel("                                    ");
		JPanel p = new JPanel();
		KeywordFrame()
		{
			KeywordLabel1 = new JLabel("대화의 내용이 적습니다");
			p.add(KeywordLabel1);

			add(p);
			setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

			this.setBounds(900, 300, 200, 200);
			this.setVisible(true);
		}
		KeywordFrame(String[] Keyword, String[] count)
		{
			KeywordLabel1 = new JLabel("           " + Keyword[0]+"  "+count[0] + "개         ");
			KeywordLabel2 = new JLabel("           " + Keyword[1]+"  "+count[1] + "개         ");
			KeywordLabel3=  new JLabel("           " + Keyword[2]+"  "+count[2] + "개         ");

			p.add(KeywordLabel1);
			p.add(term1);
			p.add(KeywordLabel2);
			p.add(term2);
			p.add(KeywordLabel3);

			add(p);

			setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

			this.setBounds(900, 300, 200, 200);
			this.setVisible(true);

		}
	}
}
class Imageprint extends Frame {
	Image img;
	String file_name;
	static String id = Id.getId();
	public Imageprint(String file_name) {
		super(id);
		this.file_name = file_name;
		setBounds(100, 100, 300, 300);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
	public void paint(java.awt.Graphics g) {

		//이클립스의 src 폴더에 이미지를 넣으면 자동으로 bin폴더에 이미지 파일이 복사됨. 그러므로 getResource 로 가져올 수 있음
		//img = Toolkit.getDefaultToolkit().getImage(ImageTest.class.getResource("").getPath()+"testpic.jpg");
		img = Toolkit.getDefaultToolkit().getImage(file_name); 
		g.drawImage(img, 0, 0, 300, 300, this);  // g.drawImage(이미지, x좌표, y좌표, 높이, 너비, this)
	}
}

