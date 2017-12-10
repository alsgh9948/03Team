
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.StringTokenizer;
import java.util.Scanner;
//keyword ���̺��� �÷����� num�� token�� �ִ�. num�� ����� �����̰�, token�� ��ūȭ�� �ܾ��̴�.
public class DB {
	Connection con = null;
	java.sql.Statement st = null;
	ResultSet rs = null;
	DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatting","root", "1234");
			st = con.createStatement();
		}
		catch(ClassNotFoundException e)
		{
			System.out.println("JDBC ����̹��� �����ϴ�.");
		}
		catch (SQLException e)
		{
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	void DB_input(String str)	//DB�� ������ ����
	{
		StringTokenizer token_str = new StringTokenizer(str,", .��!?/;'+-�����̰������Ǹ���*");	//str�� ��ūȭ�Ͽ� token_str�� ����
		int token_count = token_str.countTokens();			//��ū ���� token_count�� ����

		try {
			rs = st.executeQuery("SELECT COUNT(*) FROM keyword");		//keyword ���̺��� row ������ COUNT(*) �÷��� ����, ������� rs�� ����
			int DbRowNum = 0;		//DB�� ����� ���� ����
			while(rs.next())		//rs �� COUNT(*) �÷��� �����͸� int������ ����ȯ �� n�� ����
			{DbRowNum = Integer.parseInt(rs.getString("COUNT(*)"));}
			for(int i=1 ; i<=token_count ; i++)
			{
				String inp = "INSERT keyword VALUES ('"+(DbRowNum+i)+"','"+token_str.nextToken()+"')"; 	
				//DB�� num, token ������ ����. num�� 1������ �����ϰ�, ���� ���̺��� row ������ ��ū�� ������ŭ ���س�����.
				st.executeUpdate(inp);
				//DB�� inp ��ɾ �����Ͽ� ������Ʈ ��Ų��.
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	void print_data(String[] keyword, String[] count)		//DB���� ���� ���� ���� 3���� �ܾ �����Ѵ�. 
	{
		try {
			rs=st.executeQuery("SELECT token, COUNT(*) AS count FROM keyword GROUP BY token order by count DESC limit 3");
			
			int i = 0;
			
			while(rs.next())
			{
				keyword[i] = rs.getString("token");		//��ūȭ�� �ܾ�
				count[i++] = rs.getString("count");		//����
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		} 
		
	}
	void delete_data()			//���̺��� ��� ������ ����
	{
		try {
			st.executeUpdate("DELETE FROM keyword");
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
}