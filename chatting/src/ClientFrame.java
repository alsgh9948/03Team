
import java.awt.Color;
import java.awt.FileDialog;
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
				if(e.getKeyCode()==10){
					wt.sendMsg(null);	
					cf.isFirst = false;
					cf.setVisible(true);
					dispose();
				}
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

	String id;
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
				if(e.getKeyCode()==10){
					String id = Id.getId();
					try{
						txtA.append("["+id+"] "+ txtF.getText()+"\n");
						wt.sendMsg(null);
						txtF.setText("");
					}
					catch (Exception ee) {
						System.out.println(ee.getMessage());
					} 
				}
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
		JPanel p = new JPanel();
		paintFrame()
		{
			super("이모티콘");
			p.add(makebtn);
			p.add(loadbtn);
			add(p);

			makebtn.addActionListener(this);
			loadbtn.addActionListener(this);
			setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
			this.setBounds(900, 300, 300, 100);
			this.setVisible(true);
		}
		public void actionPerformed(ActionEvent e){
			if(e.getSource()==makebtn){
				Runtime run = Runtime.getRuntime();
				try {
					run.exec("C:\\WINDOWS\\system32\\mspaint.exe");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if(e.getSource()==loadbtn) //보내기
			{
				try {
					filecheck = false;

					Socket clientSocket = new Socket("127.0.0.1", 4001);

					FileDialog fdlg = new FileDialog(this, "열기", FileDialog.LOAD);
					fdlg.setVisible(true);
					String dir = fdlg.getDirectory();
					String file = fdlg.getFile();
					File imageFile = new File(dir+file);
					//파일 이름 전송용 스트림 변수 생성
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
							clientSocket.getOutputStream()));
					bw.write(imageFile.getName() + "\n");
					bw.flush();

					BufferedImage bi = ImageIO.read(imageFile);
					//파일 전송용 객체스트림 생성
					ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
					ImageIO.write(bi, "jpg", oos);

					bw.close();
					oos.close();

					wt.sendMsg(file+"((@!");

					System.out.println("THE END --- CLIENT");
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
		//img = Toolkit.getDefaultToolkit().getImage(ImageTest.class.getResource("").getPath()+"testpic.jpg");
		img = Toolkit.getDefaultToolkit().getImage(file_name); 
		g.drawImage(img, 0, 0, 200, 200, this);  // g.drawImage(이미지, x좌표, y좌표, 높이, 너비, this)
	}
}

