
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class Id extends JFrame implements ActionListener{
	static JTextField tf=new JTextField(8);
	JButton btn = new JButton("입력");	
	
	
	WriteThread wt;	
	ClientFrame cf;
	public Id(){}
	public Id(WriteThread wt, ClientFrame cf) {
		super("채팅접속");		
		this.wt = wt;
		this.cf = cf;
		
		setLayout(new FlowLayout());
		add(new JLabel("아이디"));
		add(tf);
		add(btn);
	
		
		btn.addActionListener(this);
		
		
		setBounds(300, 300, 250, 100);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {		
		wt.sendMsg();	
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
	JButton btnTransfer = new JButton("전송");
	JButton btemt = new JButton("이모티콘"); //이모티콘 
	JButton btnExit = new JButton("닫기");
	boolean isFirst=true;
	JPanel p1 = new JPanel();
	Socket socket;
	WriteThread wt;
		
	public ClientFrame(Socket socket) {
		super("03팀 채팅");
		this.socket = socket;
		wt = new WriteThread(this);
		new Id(wt, this);
		
		add("Center", txtA);
		
		p1.add(txtF);
		p1.add(btnTransfer);
		p1.add(btemt);
		p1.add(btnExit);
		add("South", p1);
		
		//메세지를 전송하는 클래스 생성.
		
		btnTransfer.addActionListener(this);
	    btemt.addActionListener(this);
		btnExit.addActionListener(this); //new PaintPanel() < 고침 닫기가 실행되지않음
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(400, 400, 450, 400);
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
			wt.sendMsg();
			txtF.setText("");
		}else if(e.getSource()==btemt) //이모티콘 클릭시
		{
			javax.swing.JFrame f = 
					new javax.swing.JFrame();
				f.setDefaultCloseOperation(
						JFrame.EXIT_ON_CLOSE);
				f.add(new PaintPanel());
				f.setBounds(100,100,400,400);
				f.setVisible(true);
		}
		else {
			this.dispose();
		}
	}
}
