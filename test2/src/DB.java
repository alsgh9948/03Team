
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.StringTokenizer;
import java.util.Scanner;
//keyword 테이블의 컬럼에는 num과 token이 있다. num은 저장된 순서이고, token은 토큰화한 단어이다.
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
			System.out.println("JDBC 드라이버가 없습니다.");
		}
		catch (SQLException e)
		{
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	void DB_input(String str)	//DB에 데이터 저장
	{
		StringTokenizer token_str = new StringTokenizer(str,", .ㅋ!?/;'+-은는이가에게의를을*");	//str을 토큰화하여 token_str에 저장
		int token_count = token_str.countTokens();			//토큰 개수 token_count에 저장

		try {
			rs = st.executeQuery("SELECT COUNT(*) FROM keyword");		//keyword 테이블의 row 개수를 COUNT(*) 컬럼에 저장, 결과값을 rs에 저장
			int DbRowNum = 0;		//DB에 저장된 행의 개수
			while(rs.next())		//rs 의 COUNT(*) 컬럼의 데이터를 int형으로 형변환 후 n에 저장
			{DbRowNum = Integer.parseInt(rs.getString("COUNT(*)"));}
			for(int i=1 ; i<=token_count ; i++)
			{
				String inp = "INSERT keyword VALUES ('"+(DbRowNum+i)+"','"+token_str.nextToken()+"')"; 	
				//DB에 num, token 순으로 들어간다. num은 1번부터 시작하고, 현재 테이블의 row 개수에 토큰의 개수만큼 더해나간다.
				st.executeUpdate(inp);
				//DB에 inp 명령어를 실행하여 업데이트 시킨다.
			}
		}
		catch (SQLException e)
		{
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
	void print_data(String[] keyword, String[] count)		//DB에서 가장 많이 나온 3개의 단어를 추출한다. 
	{
		try {
			rs=st.executeQuery("SELECT token, COUNT(*) AS count FROM keyword GROUP BY token order by count DESC limit 3");
			
			int i = 0;
			
			while(rs.next())
			{
				keyword[i] = rs.getString("token");		//토큰화한 단어
				count[i++] = rs.getString("count");		//개수
			}
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		} 
		
	}
	void delete_data()			//테이블의 모든 데이터 삭제
	{
		try {
			st.executeUpdate("DELETE FROM keyword");
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
		}
	}
}