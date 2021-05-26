/* Name: Christopher Prats
   Course: CNT 4714 - Fall 2020 - Project Four
   Assignment Title: A Three-Tier Distributed Web-Based Application
   Date: December 4, 2020
 */
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import com.mysql.cj.jdbc.MysqlDataSource;
public class MySQLServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("command");
        String output = "";
        Connection connection = null;
        Statement statement = null;
        DatabaseMetaData dbMetaData;
        ResultSet resultSet = null;
        ResultSetMetaData resultSetMetaData;
        boolean isResultSet;
        int affectedRows;
        int rowCount;
        int updatedMarks;
        String temp;
        int updatedQuantity = 0;
        boolean businessLogic = false;
        //Connect to Database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project4?useTimezone=true&serverTimeZone=UTC", "root", "db_password");
        } catch (SQLException | ClassNotFoundException throwables) {
            output = "Database Connection Error";
            throwables.printStackTrace();
        }
        //Get Database Metadata and Create a Statement
        try {
            dbMetaData = connection.getMetaData();
            statement = connection.createStatement(resultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            output = "Error Retrieving Database Metadata and Creating a Statement";
        }
        //Execute the Command and Set the Output
        try {
            //Determine if Business Logic will be Needed
            if (command.toLowerCase().contains("shipments") && command.toLowerCase().contains("values")) {
                temp = command.substring(command.lastIndexOf(",") + 1, command.lastIndexOf(")")).trim();
                updatedQuantity = Integer.parseInt(temp);
                if (updatedQuantity >= 100) {
                    businessLogic = true;
                }
            }
            else if (command.toLowerCase().contains("shipments") && command.toLowerCase().contains("set")) {
                businessLogic = true;
            }
            //Create beforeShipment Table for Business logic if needed
            if (businessLogic) {
                statement.executeUpdate("drop table if exists beforeShipments");
                statement.executeUpdate("create table beforeShipments like shipments");
                statement.executeUpdate("insert into beforeShipments select * from shipments");
            }
            //Determine if the Command Modifies the Table or Returns a Table
            isResultSet = statement.execute(command);
            //If the Command Returns a Table, Display it
            if (isResultSet) {
                resultSet = statement.getResultSet();
                resultSetMetaData = resultSet.getMetaData();
                output = "<table><tr>";
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    output += "<th>" + resultSetMetaData.getColumnName(i + 1) + "</th>";
                }
                output += "</tr>";
                resultSet.last();
                rowCount = resultSet.getRow();
                for (int i = 0; i < rowCount; i++) {
                    output += "<tr>";
                    resultSet.absolute(i + 1);
                    for (int j = 0; j < resultSetMetaData.getColumnCount(); j++) {
                        output += "<td>" + resultSet.getObject(j + 1) + "</td>";
                    }
                    output += "</tr>";
                }
                output += "</table>";
            }
            //If the Command Modifies the Table, Display the Modifications and Perform Business Logic
            else {
                //Display Rows Modified
                affectedRows = statement.getUpdateCount();
                output = "<div class=\"businesslogicboxed\">The statement executed successfully. <h3>" + affectedRows + " row(s) affected.</h3>";
                //Perform Business Logic if Necessary
                if (businessLogic) {
                    output += "<h3>Business Logic Detected! - Updating Supplier Status</h3>";
                    updatedMarks = statement.executeUpdate("update suppliers set status = status + 5 where snum in (select distinct snum from shipments left join beforeShipments using (snum,pnum,jnum,quantity) where beforeShipments.snum is null and quantity >= 100)");
                    statement.executeUpdate("drop table beforeShipments");
                    output += "<h3>Business Logic Updated " + updatedMarks + " supplier status marks.</h3></div>";
                }
            }
        //Display Error Message
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            output = "<div class=\"errorboxed\"> Error executing the SQL statement: <p>" + throwables.getMessage() + "</p></div>";
        }
        //Close the Connection After Each Command
        try {
            connection.close();
        //If There is an Error Closing the Connection, Append it to the Output
        } catch (SQLException throwables) {
            output += "<p>Error Closing Connection</p>";
            throwables.printStackTrace();
        }
        HttpSession session = request.getSession();
        session.setAttribute("output", output);
        session.setAttribute("command", command);
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}