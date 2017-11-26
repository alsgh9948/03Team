
import java.awt.BasicStroke;
import java.io.*;
import java.awt.*;

public class Data implements Serializable{
	int x1, y1;
	int x2, y2;
	int w,h;
	int lineWeight;// 선의 굵기
	int style; // 0= 실선, 1=점선
	int type; // 0=선, 1=원, 2=사각형, 3=펜
	Color color; // 선의 색상
	BasicStroke stroke;	
}
