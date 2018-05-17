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

import org.jasypt.util.password.StrongPasswordEncryptor;

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
    	
    	String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
        	System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
            
            out.close();
            return;
        }
    	
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        try {
        	// Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "select * from customers where email=?";
            //Object[] param={email,password};
            
            // Declare our statement
            
         	PreparedStatement statement = dbcon.prepareStatement(query);
            // Perform the query
            //ResultSet rs = statement.executeQuery(query,param);
         	statement.setString(1,email);
         	
         	System.out.println("Debugging message" + statement.toString());
         	ResultSet rs=statement.executeQuery();
         	//System.out.println("Debugging message " + rs.next());
         	JsonArray jsonArray = new JsonArray();
         	boolean success = false;
         	try {
    		if(rs.next())
    			{
    				String encryptedPassword = rs.getString("password");
    				System.out.println("encryptedPassword:"+encryptedPassword);
    				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
    				if(success) {
    					request.getSession().setAttribute("user", new User(email));
        				
        				JsonObject responseJsonObject = new JsonObject(); 
        				//responseJsonObject.addProperty("test", "test "+ email);
        				responseJsonObject.addProperty("status", "success"); 
        				responseJsonObject.addProperty("message", "success");
        				jsonArray.add(responseJsonObject);
        				out.write(jsonArray.toString());
        				System.out.println("response: " + responseJsonObject.toString());
    				}
    				else {
    					JsonObject responseJsonObject = new JsonObject();
        				
        				responseJsonObject.addProperty("status", "fail");
    					responseJsonObject.addProperty("message", "incorrect password");
    					jsonArray.add(responseJsonObject);
        				out.write(jsonArray.toString());
        				System.out.println("response: " + responseJsonObject.toString());
    				}
    				
    			}
    		else{
    				JsonObject responseJsonObject = new JsonObject();
    				
    				responseJsonObject.addProperty("status", "fail");
    				
    				responseJsonObject.addProperty("message", "email " + email + " doesn't exist");
    				
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
