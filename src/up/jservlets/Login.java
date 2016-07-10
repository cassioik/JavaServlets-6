package up.jservlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = { "/Login" })
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection dbConnection;

	/**
	 * Default constructor.
	 */
	public Login() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		System.out.println(config.getServletName() + " : Initializing...");

		ServletContext context = getServletContext();

		String driverClassName = context.getInitParameter("driverclassname");

		String dbURL = context.getInitParameter("dburl");

		String username = context.getInitParameter("username");

		String password = context.getInitParameter("password");

		// Load the driver class
		try {
			// driverClassName = "Teste de erro da Classe";
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Classe do Driver não Carregada, contacte administrador", e);
		}

		// get a database connection
		try {
			dbConnection = DriverManager.getConnection(dbURL, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// dbConnection = null;
		System.out.println("Initialized " + dbConnection.toString());
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		try {
			dbConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String userid = request.getParameter("userid");
			String password = request.getParameter("password");
			boolean login = false;

			if (userid != null && password != null) {
				//dbConnection = null;
				Statement stmt = dbConnection.createStatement();
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM LOGIN WHERE ID='" + userid + "' AND PWD='" + password + "'");
				if (rs.next())
					login = true;
			}

			if (login) {
				// Setando o userid na sessão

				HttpSession session = request.getSession(true);

				session.setAttribute("userid", userid);

				RequestDispatcher rd = request.getRequestDispatcher("Contato.html");

				rd.forward(request, response);

				return;

			} else {
				RequestDispatcher rd = request.getRequestDispatcher("Login.html");

				rd.forward(request, response);

				return;
			}
		} catch (java.lang.NullPointerException nfe) {
			nfe.printStackTrace();
			response.sendError(500, nfe.getMessage());
		} catch (Throwable e) {
			System.out.println("Entrei na Exception");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			throw new ServletException(e);
		}
	}

}