package com.asu.smartshop;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.SortDirection;

public class SmartShopServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);

		// If this servlet is passed a cursor parameter, let's use it.
		String startCursor = req.getParameter("cursor");
		if (startCursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
		}

		Query q = new Query("Person").addSort("name", SortDirection.ASCENDING);
		PreparedQuery pq = datastore.prepare(q);

		QueryResultList<Entity> results;
		try {
			results = pq.asQueryResultList(fetchOptions);
		} catch (IllegalArgumentException e) {
			// IllegalArgumentException happens when an invalid cursor is used.
			// A user could have manually entered a bad cursor in the URL or
			// there
			// may have been an internal implementation detail change in App
			// Engine.
			// Redirect to the page without the cursor parameter to show
			// something
			// rather than an error.
			resp.sendRedirect("/people");
			return;
		}

		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter w = resp.getWriter();
		w.println("<!DOCTYPE html>");
		w.println("<meta charset=\"utf-8\">");
		w.println("<title>Cloud Datastore Cursor Sample</title>");
		w.println("<ul>");
		for (Entity entity : results) {
			w.println("<li>" + entity.getProperty("name") + "</li>");
		}
		w.println("</ul>");

		String cursorString = results.getCursor().toWebSafeString();
*/
		// This servlet lives at '/people'.
		String api_key = getServletContext().getInitParameter("API_KEY");
		resp.getWriter().println("smart shop welcome " + api_key);
	}
}
