

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;

/**
 * Servlet implementation class InsertStarServlet
 */
@WebServlet(name = "/InsertStarServlet", urlPatterns = "/api/insertstar")
public class InsertStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertStarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("application/json"); // Response mime type
		System.out.println("Confirmation Servlet receive the message");
		PrintWriter out = response.getWriter();
		String starname=request.getParameter("starname");
		String birthday=request.getParameter("birthday");
		//get the user information from the session
        //User user = (User) request.getSession().getAttribute("user");
		try {
			Connection dbcon = dataSource.getConnection();
			//String query = "select customers.id from customers where customers.email='"+user.getUsername()+"';";
			//System.out.println("query: "+query);
			String query1="select max(id) from stars;";
			Statement statement = dbcon.createStatement();
			ResultSet rs = statement.executeQuery(query1);
			String maxId=rs.getString("id");
			String[] strs = maxId.split("[^0-9]");//根据不是数字的字符拆分字符串
		    String numStr = strs[strs.length-1];//取出最后一组数字
		    if(numStr != null && numStr.length()>0){//如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
		        int n = numStr.length();//取出字符串的长度
		        int num = Integer.parseInt(numStr)+1;//将该数字加一
		        String added = String.valueOf(num);
		        n = Math.min(n, added.length());
		        //拼接字符串
		        String starId=maxId.subSequence(0, maxId.length()-n)+added;
		    }else{
		        throw new NumberFormatException();
		    }
			JsonArray jsonArray = new JsonArray();
			String query2="insert into stars values()";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

}
