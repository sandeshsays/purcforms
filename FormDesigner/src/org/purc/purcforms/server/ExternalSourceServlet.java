package org.purc.purcforms.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet that handles external source searches.
 * 
 * @author daniel
 *
 */
public class ExternalSourceServlet  extends HttpServlet {

	public static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
			
		String source = request.getParameter("ExternalSource");
		String displayField = request.getParameter("DisplayField");
		String valueField = request.getParameter("ValueField");
		String filterField = request.getParameter("FilterField");
		String filterValue = request.getParameter("FilterValue");
		
		String sql = source;
		if(!sql.startsWith("select"))
			sql = "select " + displayField + "," + valueField + " from " + source + 
			" where " + displayField + " is not null and " + valueField + " is not null ";
		
		if((filterField != null && filterField.trim().length() > 0) &&
				filterValue != null && filterValue.trim().length() > 0){
			
			sql += " and " + filterField;
			
			if(filterValue.equalsIgnoreCase("IS NULL"))
				 sql += " is null ";
			else
				sql += "='" + filterValue + "'";
		}
		
		sql += " order by " + displayField;
		
		//List<Object[]> list = xformsService.getList(sql, displayField, valueField);
		
		String result = null; //"Choice One|1$Choice Two|2$Choice Three|3$Choice Four|4$Choice Five|5"
		
		/*for(Object[] obj : list){
			
			if(obj[0] == null || obj[0].toString().trim().length() == 0)
				continue;
			
			if(obj[1] == null || obj[1].toString().trim().length() == 0)
				continue;
			
			if(result != null)
				result += "$";
			else
				result = "";
			
			result += obj[0];
			result += "|" + obj[1];
		}*/
				
		response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        response.setHeader("Cache-Control", "no-store");
        
 		response.setContentType("text/plain; charset=UTF-8");
 		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(result);
	}
}
