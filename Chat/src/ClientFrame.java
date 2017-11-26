
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
import javax.swing.JDialog;

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
	JButton btnKeyword = new JButton("키워드");
	
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
		
		p1.add(btemt);
		p1.add(txtF);
		p1.add(btnTransfer);
		p1.add(btnKeyword);
		p1.add(btnExit);
		add("South", p1);
		
		//메세지를 전송하는 클래스 생성.
		
		btnTransfer.addActionListener(this);
	    btemt.addActionListener(this);
	    btnKeyword.addActionListener(this);
		btnExit.addActionListener(this); //new PaintPanel() < 고침 닫기가 실행되지않음
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		setBounds(300, 300, 600, 400);
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
//				f.setDefaultCloseOperation(		//이모티콘의 닫기버튼 이벤트 시 프로그램 종료 되서 일단 주석
//						JFrame.DISPOSE_ON_CLOSE);
				f.add(new PaintPanel());
				f.setBounds(100,100,400,400);
				f.setVisible(true);
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
