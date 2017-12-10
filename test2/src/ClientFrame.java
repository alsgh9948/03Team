
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
				//Ŭ���̾�Ʈ�� ���� ���ڿ� �ޱ�								
				byte b[] = new byte[255];
				n=bin.read(b);				

				//��밡 ������ ������ break;
				if(n==-1){
					//���Ϳ��� ���ֱ�
					vec.remove(socket);
					break;
				}
				System.out.println("���ϼ����� sendMsgȣ��");
				//����� ���ϵ��� ���ؼ� �ٸ� Ŭ���̾�Ʈ���� ���ڿ� �����ֱ�
				sendMsg(b);								
			}

		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}finally{
			try{
				if(bin != null) bin.close();
				if(socket != null) {
					System.out.println("socketclose ȣ��");
					socket.close();
				}
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}

	//���۹��� ���ڿ� �ٸ� Ŭ���̾�Ʈ�鿡�� �����ִ� �޼���
	public void sendMsg(byte b[]){
		//		String str1 = new String(b);
		//		System.out.println(str1);
		try{
			for(Socket socket:vec){
				//for�� ���� ������ socket�� �����͸� ���� Ŭ���̾�Ʈ�� ��츦 �����ϰ� 
				//������ socket�鿡�Ը� �����͸� ������.
				if(socket != this.socket){
					BufferedOutputStream bout  = new BufferedOutputStream(
							socket.getOutputStream());
					bout.write(b);
					bout.flush();
					//��,���⼭ ���� ���ϵ��� ���ǰ͵��̱� ������ ���⼭ ������ �ȵȴ�.
				}
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
class Id extends JFrame implements ActionListener{
	static JTextField tf=new JTextField(8);
	JButton btn = new JButton("�Է�");	
	WriteThread wt;	
	ClientFrame cf;
	public Id(){}
	public Id(final WriteThread wt, final ClientFrame cf) {
		super("ä������");		
		this.wt = wt;
		this.cf = cf;
		setLayout(new FlowLayout());
		add(new JLabel("���̵�"));
		add(tf);
		add(btn);

		//����Ű ����
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
	JButton btnTransfer = new JButton("����");
	JButton btemt = new JButton("�̸�Ƽ��"); //�̸�Ƽ�� 
	JButton btnExit = new JButton("�ݱ�");
	JButton btnKeyword = new JButton("Ű����");

	String file_name;
	boolean filecheck = true;
	boolean isFirst=true;
	JPanel p1 = new JPanel();
	Socket socket;
	WriteThread wt;

	public ClientFrame(Socket socket) {
		super("03�� ä��");
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

		//����Ű ����
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
		btnExit.addActionListener(this); //new PaintPanel() < ��ħ �ݱⰡ �����������
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		setBounds(300, 300, 500, 400);
		setVisible(false);	
	}

	public void actionPerformed(ActionEvent e){
		String id = Id.getId();
		if(e.getSource()==btnTransfer){//���۹�ư ������ ���
			//�޼��� �Է¾��� ���۹�ư�� ������ ���
			if(txtF.getText().equals("")){
				return;
			}			
			txtA.append("["+id+"] "+ txtF.getText()+"\n");
			wt.sendMsg(null);
			txtF.setText("");
		}else if(e.getSource()==btemt) //�̸�Ƽ�� Ŭ����
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
		JButton makebtn = new JButton("�����");
		JButton loadbtn = new JButton("������");
		JLabel Label1 = new JLabel("���� ��� : ����ȭ�� image ����");
		JLabel Label2 = new JLabel("���� �̸� : image.png");
		JPanel p = new JPanel();
		paintFrame()
		{
			super("�̸�Ƽ��");
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
			else if(e.getSource()==loadbtn) //������
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
					txtA.append("["+id+"] "+"�� �����Ͽ����ϴ�.\n");
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
			KeywordLabel1 = new JLabel("��ȭ�� ������ �����ϴ�");
			p.add(KeywordLabel1);

			add(p);
			setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);

			this.setBounds(900, 300, 200, 200);
			this.setVisible(true);
		}
		KeywordFrame(String[] Keyword, String[] count)
		{
			KeywordLabel1 = new JLabel("           " + Keyword[0]+"  "+count[0] + "��         ");
			KeywordLabel2 = new JLabel("           " + Keyword[1]+"  "+count[1] + "��         ");
			KeywordLabel3=  new JLabel("           " + Keyword[2]+"  "+count[2] + "��         ");

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

		//��Ŭ������ src ������ �̹����� ������ �ڵ����� bin������ �̹��� ������ �����. �׷��Ƿ� getResource �� ������ �� ����
		//img = Toolkit.getDefaultToolkit().getImage(ImageTest.class.getResource("").getPath()+"testpic.jpg");
		img = Toolkit.getDefaultToolkit().getImage(file_name); 
		g.drawImage(img, 0, 0, 300, 300, this);  // g.drawImage(�̹���, x��ǥ, y��ǥ, ����, �ʺ�, this)
	}
}

