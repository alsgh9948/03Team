
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JComboBox;
import java.awt.Dimension;

public class PaintPanel extends JPanel
	implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1L;
	public JPanel menuPanel = null;
	private JComboBox jComboBox = null;
	private JButton jButton = null;
	private JTextField jTextField = null;
	private JComboBox jComboBox1 = null;
	private JLabel jLabel = null;
	/**
	 * This is the default constructor
	 */
	int type=0 ; // 동형의 종류
	Color lineColor=null; //선의 색상
	int lineWeight=1 ;// 선의 굵기
	int style=0 ; // 선의 스타일
	
	int x1, y1, x2, y2;
	int w, h;
	int ox, oy;
	BasicStroke bs = new BasicStroke();  //  @jve:decl-index=0:
	Graphics2D gg = null;  //  @jve:decl-index=0:
	Graphics g = null;
	
	Vector vector = new Vector(20,10);  //  @jve:decl-index=0:
	
	
	public PaintPanel() {
		super();
		initialize();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(471, 294);
		this.setLayout(new BorderLayout());
		this.add(getMenuPanel(), BorderLayout.NORTH);
	}

	/**
	 * This method initializes menuPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getMenuPanel() {
		if (menuPanel == null) {
			jLabel = new JLabel();
			jLabel.setText("선의 굵기");
			menuPanel = new JPanel();
			menuPanel.setLayout(new BoxLayout(getMenuPanel(), BoxLayout.X_AXIS));
			menuPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			menuPanel.setBackground(new Color(204, 204, 255));
			menuPanel.add(getJButton(), null);
			menuPanel.add(getJComboBox(), null);
			menuPanel.add(jLabel, null);
			menuPanel.add(getJTextField(), null);
			menuPanel.add(getJComboBox1(), null);
			menuPanel.add(getJButton2(), null);
			
		}
		return menuPanel;
	}

	/**
	 * This method initializes canvas	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public void mousePressed(java.awt.event.MouseEvent e) {
		g = getGraphics();
		gg = (Graphics2D)g;
		lineWeight = Integer.parseInt(jTextField.getText());
		if( style == 0){ // 실선
			float f[] = { lineWeight, 0f};
			bs = new BasicStroke(lineWeight, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_MITER,10f, f, 0f);
		}else{// 점선
			float f[] = { lineWeight, lineWeight*2};
			bs = new BasicStroke(lineWeight, 
					BasicStroke.CAP_SQUARE, 
					BasicStroke.JOIN_MITER,10f, f, 0f);
			
		}

		gg.setStroke(bs);
		g.setColor(lineColor);
		
		x1 = e.getX();
		y1 = e.getY();
		
		x2 = x1;
		y2 = y1;
		ox = x1;
		oy = y1;
		
		w = 0;
		h = 0;

	}
	
	public void mouseDragged(java.awt.event.MouseEvent e) {
		ox = x2;
		oy = y2;
		
		x2 = e.getX();
		y2 = e.getY();
		
		switch(type){
		case 0 : line();break; // 선그리기
		case 1 : oval();break; // 원그리기
		case 2 : rect();break; // 사각형 그리기
		case 3 : pen();break; // 펜기능
		}
	}
	
	public void mouseReleased(java.awt.event.MouseEvent e) {
		
		w = x2 - x1;
		h = y2 - y1;
		gg.setPaintMode();
		switch(type){
		case 0: g.drawLine(x1,y1, x2, y2);break;
		case 1: g.drawOval(x1, y1, w, h);break;
		case 2: g.drawRect(x1, y1, w, h);break;
		}
		
		// 벡터에 저장
		Data d = new Data();
		d.x1 = x1; d.y1 = y1;
		d.x2 = x2; d.y2 = y2;
		d.w = w; d.h = h;
		d.color = lineColor;
		d.stroke = bs;
		d.style = style;
		d.type = type;
		d.lineWeight = lineWeight;
		
		vector.add(d);					
		
	}

	public void mouseClicked(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	public void line(){
		g.setXORMode(Color.white);
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x1, y1, ox, oy);
	}
	public void oval(){// 원
		g.setXORMode(Color.white);
		w = x2-x1; h=y2-y1;
		g.drawOval(x1, y1, w,h);
		g.drawOval(x1,y1, (ox-x1), (oy-y1));
	}
	public void rect(){
		g.setXORMode(Color.white);
		w = x2-x1; h=y2-y1;
		g.drawRect(x1, y1, w,h);
		g.drawRect(x1,y1, (ox-x1), (oy-y1));
		
	}
	public void pen(){
		g.drawLine(x1, y1, x2, y2);
		
		// 벡터에 저장
		Data d = new Data();
		d.x1 = x1; d.y1 = y1;
		d.x2 = x2; d.y2 = y2;
		d.w = w; d.h = h;
		d.color = lineColor;
		d.stroke = bs;
		d.style = style;
		d.type = type;
		d.lineWeight = lineWeight;
		
		vector.add(d);	
		x1 = x2;
		y1 = y2;
		
	}
	
	// 화면을 갱신하는 로직
	// paint()재정의
	
	public void paint(Graphics g){

		gg = (Graphics2D)g;
		Data d = null;
		for(int i=0 ; i<vector.size() ; i++){
			d = (Data)vector.get(i);
			g.setColor(d.color);
			gg.setStroke(d.stroke);
			g.setPaintMode();
		
			switch(d.type){
			case 0: g.drawLine(d.x1, d.y1, d.x2, d.y2);break;
			case 1: g.drawOval(d.x1, d.y1, d.w,  d.h);break;
			case 2: g.drawRect(d.x1, d.y1, d.w,  d.h);break;
			case 3: g.drawLine(d.x1, d.y1, d.x2, d.y2);break;
			}
		}
		menuPanel.repaint();
	}
	
	
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setPreferredSize(new Dimension(71, 27));
			jComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(java.awt.event.ItemEvent.SELECTED 
							== e.getStateChange()){
						type = jComboBox.getSelectedIndex();
						
					}
				}
			});
			jComboBox.addItem("선");
			jComboBox.addItem("원");
			jComboBox.addItem("사각");
			jComboBox.addItem("펜");
		}
		return jComboBox;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton("선색상");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JColorChooser jcc = new JColorChooser();
					lineColor = 
						jcc.showDialog(jButton.getParent(), "선색", lineColor);
				}
			});
		}
		return jButton;
	}
	
	private JButton getJButton2() {
			jButton = new JButton("보내기");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
				}
			});
		return jButton;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField("1");
			jTextField.setSize(50,20);
		}
		return jTextField;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox1() {
		if (jComboBox1 == null) {
			jComboBox1 = new JComboBox();
			jComboBox1.setPreferredSize(new Dimension(71, 27));
			jComboBox1.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if(e.getStateChange() == java.awt.event.ItemEvent.SELECTED){
						style = jComboBox1.getSelectedIndex();
					}
				}
			});
			jComboBox1.addItem("실선");
			jComboBox1.addItem("점선");
		}
		return jComboBox1;
	}


	public static void main(String args[]){
		javax.swing.JFrame f = 
			new javax.swing.JFrame();
		f.setDefaultCloseOperation(
				JFrame.EXIT_ON_CLOSE);
		f.add(new PaintPanel());
		f.setBounds(100,100,400,400);
		f.setVisible(true);
		
	}

}  //  @jve:decl-index=0:visual-constraint="144,62"
