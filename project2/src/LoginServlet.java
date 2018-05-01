

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import javax.annotation.Resource;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	response.setContentType("application/json"); // Response mime type
    	GenericServlet console = null;
		console.log("hello world");
    	PrintWriter out = response.getWriter();
    	
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
        	// Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select * from customers where email=? and password=?";
            //Object[] param={email,password};
            
            // Declare our statement
            
         	PreparedStatement statement = dbcon.prepareStatement(query);
            // Perform the query
            //ResultSet rs = statement.executeQuery(query,param);
         	statement.setString(1,email);
         	statement.setString(2,password);
         	ResultSet rs=statement.executeQuery();
    		if(rs.next())
    			{
    				request.getSession().setAttribute("user", new User(email));
    				JsonObject responseJsonObject = new JsonObject();
    				responseJsonObject.addProperty("status", "success");
    				responseJsonObject.addProperty("message", "success");
    				response.getWriter().write(responseJsonObject.toString());
    			}
    		else{
    				JsonObject responseJsonObject = new JsonObject();
    				responseJsonObject.addProperty("status", "fail");
    				String query1 = "select * from customers where email=?";
    				PreparedStatement statement1 = dbcon.prepareStatement(query1);
    				statement1.setString(1,email);
    				ResultSet rs1 = statement1.executeQuery();
    				if(!rs.next()) {
    					responseJsonObject.addProperty("message", "email" + email + " doesn't exist");
    				}
    				else{
    					responseJsonObject.addProperty("message", "incorrect password");
    				}
    				response.getWriter().write(responseJsonObject.toString());
    			} 
    			
            JsonArray jsonArray = new JsonArray();
         // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
        	
        }catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }
}
