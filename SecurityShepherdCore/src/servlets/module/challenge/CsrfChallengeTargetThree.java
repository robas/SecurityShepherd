package servlets.module.challenge;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import utils.Validate;
import dbProcs.Getter;
import dbProcs.Setter;

/**
 * Cross Site Request Forgery challenge Target Three - Does not return result key
 * <br/><br/>
 * This file is part of the Security Shepherd Project.
 * 
 * The Security Shepherd project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.<br/>
 * 
 * The Security Shepherd project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br/>
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Security Shepherd project.  If not, see <http://www.gnu.org/licenses/>. 
 * @author Mark Denihan
 *
 */
public class CsrfChallengeTargetThree extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(CsrfChallengeTargetThree.class);
	/**
	 * CSRF vulnerable function that can be used by users to force other users to mark their CSRF challenge Three as complete.
	 * @param userId User identifier to be incremented
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException
	{
		log.debug("Cross-SiteForegery Challenge Three Target Servlet");
		PrintWriter out = response.getWriter();  
		out.print(getServletInfo());
		try
		{
			boolean result = false;
			HttpSession ses = request.getSession(true);
			if(Validate.validateSession(ses))
			{
				String plusId = request.getParameter("userid");
				log.debug("User Submitted - " + plusId);
				String csrfParam = null;
				if(request.getParameter("csrfToken") != null)
				{
					csrfParam = (String)request.getParameter("csrfToken");
					if(csrfParam.isEmpty())
						csrfParam = null;
				}
				
				String userId = (String)ses.getAttribute("userStamp");
				if(!userId.equals(plusId) && csrfParam != null)
				{
					String ApplicationRoot = getServletContext().getRealPath("");
					String userName = (String)ses.getAttribute("userName");
					String attackerName = Getter.getUserName(ApplicationRoot, plusId);
					if(attackerName != null)
					{
						log.debug(userName + " is been CSRF'd by " + attackerName);
						
						log.debug("Attemping to Increment ");
						String moduleHash = z6b2f5ebbe112dd09a6c430a167415820adc5633256a7b44a7d1e262db105e3c.class.getSimpleName();
						String moduleId = Getter.getModuleIdFromHash(ApplicationRoot, moduleHash);
						result = Setter.updateCsrfCounter(ApplicationRoot, moduleId, plusId);
					}
					else
					{
						log.error("UserId '" + plusId + "' could not be found.");
					}
				}
				else
				{
					log.debug("No CSRF Token found");
				}
				
				if(result)
				{
					out.write("Increment Successful");
				}
				else
				{
					out.write("Increment Failed");
				}
			}
			else
			{
				out.write("Invalid Session Detected");
			}
		}
		catch(Exception e)
		{
			out.write("An Error Occured! You must be getting funky!");
			log.fatal("Cross Site Request Forgery Challenge Target 3 - " + e.toString());
		}
	}
}