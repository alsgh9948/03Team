
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
	JButton btn = new JButton("�Է�");	
	
	
	WriteThread wt;	
	ClientFrame cf;
	public Id(){}
	public Id(WriteThread wt, ClientFrame cf) {
		super("ä������");		
		this.wt = wt;
		this.cf = cf;
		
		setLayout(new FlowLayout());
		add(new JLabel("���̵�"));
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
	JButton btnTransfer = new JButton("����");
	JButton btemt = new JButton("�̸�Ƽ��"); //�̸�Ƽ�� 
	JButton btnExit = new JButton("�ݱ�");
	JButton btnKeyword = new JButton("Ű����");
	
	boolean isFirst=true;
	JPanel p1 = new JPanel();
	Socket socket;
	WriteThread wt;
		
	public ClientFrame(Socket socket) {
		super("03�� ä��");
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
		
		//�޼����� �����ϴ� Ŭ���� ����.
		
		btnTransfer.addActionListener(this);
	    btemt.addActionListener(this);
	    btnKeyword.addActionListener(this);
		btnExit.addActionListener(this); //new PaintPanel() < ��ħ �ݱⰡ �����������
		setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		setBounds(300, 300, 600, 400);
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
			wt.sendMsg();
			txtF.setText("");
		}else if(e.getSource()==btemt) //�̸�Ƽ�� Ŭ����
		{
			javax.swing.JFrame f = 
					new javax.swing.JFrame();
//				f.setDefaultCloseOperation(		//�̸�Ƽ���� �ݱ��ư �̺�Ʈ �� ���α׷� ���� �Ǽ� �ϴ� �ּ�
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
