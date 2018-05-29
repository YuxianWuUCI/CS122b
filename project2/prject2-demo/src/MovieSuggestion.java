import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/**
 * Servlet implementation class MovieSuggestion
 */
@WebServlet(name = "/MovieSuggestion", urlPatterns = "/api/movie-suggestion")
public class MovieSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieSuggestion() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			// get the query string from parameter
			String query = request.getParameter("query");
			System.out.println("query: "+query);
			// return the empty json array if query is null or empty
			if (query == null || query.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				System.out.println("not search");
				return;
			}
			System.out.println("query2: "+query);
			Connection dbcon = dataSource.getConnection();
			System.out.println("query3: "+query);
			String dbquery = "select id, title from movies where match(title) against('";
			//search the suggestion movie according to the content in input box
			if(query!=null && !query.equalsIgnoreCase("null")) {
				String[] titlelist = query.split(" ");
				for (String retval: titlelist){
		           dbquery = dbquery.concat(" +"+retval+"*");
		        }
				dbquery = dbquery.concat("' IN BOOLEAN MODE);");
			}
			System.out.println("query: "+dbquery);
			PreparedStatement statement = dbcon.prepareStatement(dbquery);
			// Perform the query
			ResultSet rs = statement.executeQuery();
			
			//Write the result to the JsonArray
			//the length of the suggestion list should not be more than 10
			int count = 0;
			while(rs.next()&&count<10) {
				count++;
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("value", rs.getString("title"));
				
				JsonObject additionalDataJsonObject = new JsonObject();
				additionalDataJsonObject.addProperty("category", "Movie");
				additionalDataJsonObject.addProperty("ID", rs.getString("id"));
				jsonObject.add("data", additionalDataJsonObject);
				jsonArray.add(jsonObject);
			}
			
			rs.close();
			statement.close();
			dbcon.close();
			response.getWriter().write(jsonArray.toString());
			return;
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
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
