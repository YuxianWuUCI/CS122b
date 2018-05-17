import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServelet
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServelet extends HttpServlet {
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
	
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServelet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json"); // Response mime type
    	PrintWriter out = response.getWriter();
    	String flag=request.getParameter("optionsRadiosinline");
    	System.out.println(flag);
    	
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(email);
        System.out.println(password);
        try {
        	// Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            System.out.println("build connection");
            if(flag.equals("option1")) {
            	
            	String query = "select * from customers where email=? and password=?";
        
            	//String query = "select * from customers where email=? and password=?";
            	//Object[] param={email,password};
            
            	// Declare our statement
            
            	PreparedStatement statement = dbcon.prepareStatement(query);
         		// Perform the query
         		//ResultSet rs = statement.executeQuery(query,param);
         		statement.setString(1,email);
         		statement.setString(2,password);
         		System.out.println("Debugging message" + statement.toString());
         		ResultSet rs=statement.executeQuery();
         		//System.out.println("Debugging message " + rs.next());
         		JsonArray jsonArray = new JsonArray();
         		try {
         		if(rs.next())
    				{
    					request.getSession().setAttribute("user", new User(email));
    					System.out.println("in next");
    					JsonObject responseJsonObject = new JsonObject(); 
    					//responseJsonObject.addProperty("test", "test "+ email);
    					responseJsonObject.addProperty("identification", "user"); 
    					responseJsonObject.addProperty("status", "success"); 
    					responseJsonObject.addProperty("message", "success");
    					jsonArray.add(responseJsonObject);
    					out.write(jsonArray.toString());
    					System.out.println("response: " + responseJsonObject.toString());
    				}
    			else{
    					JsonObject responseJsonObject = new JsonObject();
    				
    					responseJsonObject.addProperty("status", "fail");
    				
    					String query1 = "select * from customers where email=?";
    					PreparedStatement statement1 = dbcon.prepareStatement(query1);
    					statement1.setString(1,email);
    					ResultSet rs1 = statement1.executeQuery();
    					//responseJsonObject.addProperty("test", "test "+ email);
    					if(!rs1.next()) {
    						responseJsonObject.addProperty("message", "email " + email + " doesn't exist");
    					}
    					else{
    						responseJsonObject.addProperty("message", "incorrect password");
    					}
    					jsonArray.add(responseJsonObject);
    					out.write(jsonArray.toString());
    					System.out.println("response: " + jsonArray.toString());
    				
    			} 
         	}catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
         	// write JSON string to output
            //out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
       
            }
        
        
        
            else if(flag.equals("option2")) {

            	String query = "select * from employees where email=? and password=?";
        
            	//String query = "select * from customers where email=? and password=?";
            	//Object[] param={email,password};
            
            	// Declare our statement
            
            	PreparedStatement statement = dbcon.prepareStatement(query);
         		// Perform the query
         		//ResultSet rs = statement.executeQuery(query,param);
         		statement.setString(1,email);
         		statement.setString(2,password);
         		System.out.println("Debugging message" + statement.toString());
         		ResultSet rs=statement.executeQuery();
         		//System.out.println("Debugging message " + rs.next());
         		JsonArray jsonArray = new JsonArray();
         		try {
         		if(rs.next())
    				{
    					request.getSession().setAttribute("user", new User(email));
    				
    					JsonObject responseJsonObject = new JsonObject(); 
    					//responseJsonObject.addProperty("test", "test "+ email);
    					responseJsonObject.addProperty("identification", "employee"); 
    					responseJsonObject.addProperty("status", "success"); 
    					responseJsonObject.addProperty("message", "success");
    					jsonArray.add(responseJsonObject);
    					out.write(jsonArray.toString());
    					System.out.println("response: " + responseJsonObject.toString());
    				}
    			else{
    					JsonObject responseJsonObject = new JsonObject();
    				
    					responseJsonObject.addProperty("status", "fail");
    				
    					String query1 = "select * from employees where email=?";
    					PreparedStatement statement1 = dbcon.prepareStatement(query1);
    					statement1.setString(1,email);
    					ResultSet rs1 = statement1.executeQuery();
    					//responseJsonObject.addProperty("test", "test "+ email);
    					if(!rs1.next()) {
    						responseJsonObject.addProperty("message", "email " + email + " doesn't exist");
    					}
    					else{
    						responseJsonObject.addProperty("message", "incorrect password");
    					}
    					jsonArray.add(responseJsonObject);
    					out.write(jsonArray.toString());
    					System.out.println("response: " + jsonArray.toString());
    				
    			} 
         	}catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
         	// write JSON string to output
            //out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
       
            
            }
        
        
        
        
        
        
        
        
        
        
        
        
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
