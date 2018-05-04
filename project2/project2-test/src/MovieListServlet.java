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

/**
 * Servlet implementation class MovieListServlet
 */
@WebServlet(name = "/MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieListServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		//These three parameters need to be processed in pattern
		String title = request.getParameter("title");
		System.out.println("title: "+title);
		String starname = request.getParameter("starname");
		System.out.println("starname: "+starname);
		System.out.println("null: "+starname.equalsIgnoreCase("null"));
		String director = request.getParameter("director");
		
		//The rest parameters need not to be processed in pattern
		String year = request.getParameter("year");
		/**
		 * Default sort would be sort on title ascend 
		 **/
		String sortontitle = request.getParameter("sortontitle");
		String sortonrating = request.getParameter("sortonrating");
		/**
		 * These two parameters need to have default value
		 * offset = 0
		 * range = 20
		**/
		String offset = request.getParameter("offset");
		String range = request.getParameter("range");
		
		
		

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			// Construct a query with parameter represented by "?"
			String query = "select m.id, m.title, m.year, m.director"
            		+ ", g.name, s.name, r.rating "
            		+ "from movies as m, genres_in_movies as gim,"
            		+ "genres as g, stars as s, stars_in_movies as sim,"
            		+ "ratings as r where m.id = gim.movieId and "
            		+ "gim.genreId = g.id and m.id = sim.movieId and "
            		+ "sim.starId = s.id and m.id = r.movieId";
			/*
			 * This part is processing the parameters received by using url
			 * Program should consider about the occasion that some parameter might not exist
			 */
			if(!title.equalsIgnoreCase("null")) {
				query = query.concat(" and m.title like \""+title+"%\"");
			}
			
			if(!starname.equalsIgnoreCase("null")) {
				query = query.concat(" and s.name like \""+starname+"%\"");
			}
			
			if(!director.equalsIgnoreCase("null")) {
				query = query.concat(" and m.director like \""+director+"%\"");
			}
			
			if(!year.equalsIgnoreCase("null")) {
				query = query.concat(" and m.year = "+year);
			}
			
			if(sortontitle!=null) {
				query = query.concat(" order by m.title "+sortontitle);
			}else if(sortonrating!=null){
				//if user chooses sorting on rating
				query = query.concat(" order by r.rating "+sortonrating);
			}else {
				//default is sort on title ascend
				query = query.concat(" order by m.title");
			}
			
			if(offset==null) {
				offset="0";
			}
			if(range==null) {
				range = "20";
			}
			query = query.concat(" limit 300 offset "+offset+";");
			System.out.println("query: "+query);
			// Declare our statement
			Statement statement = dbcon.createStatement();
			// Perform the query
			ResultSet rs = statement.executeQuery(query);
			JsonArray jsonArray = new JsonArray();
			int count = 1;
			String temp_Id = "";
    		String temp_movieTitle = "";
    		String temp_year = "";
    		String temp_rating = "";
    		String temp_director = "";
    		String Genre="";
    		String Star="";
    		try {
    			int rangeint = Integer.parseInt(range);
    			// Iterate through each row of rs
    			System.out.println("rangeint: "+rangeint);
    			while (rs.next() && count<=rangeint) {

    				String Id = rs.getString("m.id");
    				String Title = rs.getString("m.title");
    				String Year = rs.getString("m.year");
    				String Director = rs.getString("m.director");
    				String Genresname = rs.getString("g.name");
    				String Starsname = rs.getString("s.name");
    				String Rating = rs.getString("r.rating");
    				//if the movie in this row is the same movie 
    				//in the previous row with different genre or star 
    				if(temp_movieTitle.contains(Title)){
        	        	//if this row has a different genre, add it to the genre
    					if(!Genre.contains(Genresname)) {
        	        		Genre = Genre.concat(", "+Genresname);
        	        	}
    					//if this row has a different genre, add it to the genre
        	        	if(!Star.contains(Starsname)) {
        	        		Star = Star.concat(", "+Starsname);
        	        	}
    				}else {
    					//if this row has a different movie, web should print the total information of the previous page
        	        	if(count==1) {
            	        	Genre = Genresname;
            	        	Star = Starsname;
            	        	temp_Id = Id;
            	        	temp_movieTitle = Title;
            	        	temp_year = Year;
            	        	temp_rating = Rating;
            	        	temp_director = Director;
            	        	count ++;
            	        	continue;
        	        	}
        	        	// Create a JsonObject based on the data we retrieve from rs

        				JsonObject jsonObject = new JsonObject();
        				jsonObject.addProperty("movie_id", temp_Id);
        				jsonObject.addProperty("movie_title", temp_movieTitle);
        				jsonObject.addProperty("movie_year", temp_year);
        				jsonObject.addProperty("movie_director", temp_director);
        				jsonObject.addProperty("movie_genre", Genre);
        				jsonObject.addProperty("movie_star", Star);
        				jsonObject.addProperty("movie_director", temp_director);
        				jsonObject.addProperty("movie_rating", temp_rating);
        				Genre = Genresname;
        	        	Star = Starsname;
        	        	temp_Id = Id;
        	        	temp_movieTitle = Title;
        	        	temp_year = Year;
        	        	temp_rating = Rating;
        	        	temp_director = Director;
        	        	count ++;
        				jsonArray.add(jsonObject);
    				
    				}
    			}
    			JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", temp_Id);
				jsonObject.addProperty("movie_title", temp_movieTitle);
				jsonObject.addProperty("movie_year", temp_year);
				jsonObject.addProperty("movie_director", temp_director);
				jsonObject.addProperty("movie_genre", Genre);
				jsonObject.addProperty("movie_star", Star);
				jsonObject.addProperty("movie_director", temp_director);
				jsonObject.addProperty("movie_rating", temp_rating);
				jsonArray.add(jsonObject);
    			System.out.println("count: "+count);
    			System.out.println("jsonArray: "+jsonArray.toString());
                // write JSON string to output
                out.write(jsonArray.toString());
                // set response status to 200 (OK)
                response.setStatus(200);
    		}catch (NumberFormatException e) {
    		    e.printStackTrace();
    		}
    		

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		
		out.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}