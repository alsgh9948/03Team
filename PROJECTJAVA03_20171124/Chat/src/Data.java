
import java.awt.BasicStroke;
import java.io.*;
import java.awt.*;

public class Data implements Serializable{
	int x1, y1;
	int x2, y2;
	int w,h;
	int lineWeight;// ���� ����
	int style; // 0= �Ǽ�, 1=����
	int type; // 0=��, 1=��, 2=�簢��, 3=��
	Color color; // ���� ����
	BasicStroke stroke;	
}
