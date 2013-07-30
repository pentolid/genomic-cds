
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.text.StrSubstitutor;

import safetycode.MedicineSafetyProfileOWLAPI;
import utils.StringReader;

import exception.BadFormedBase64NumberException;
import exception.VariantDoesNotMatchAnAllowedVariantException;


/**
 * Servlet implementation class SafetyCodeInterpreter
 */

public class SafetyCodeInterpreter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private MedicineSafetyProfileOWLAPI bootstrapProfile;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		// initialize bootstrapProfile
		String path = this.getServletContext().getRealPath("/");
		path=path.replaceAll("\\\\", "/");
		bootstrapProfile = new MedicineSafetyProfileOWLAPI(path+"MSC_classes.ttl");
		
		
		PrintWriter out = response.getWriter();
		
		String base64ProfileString = request.getParameter("code");
		
		if ((base64ProfileString.equals("")) || (base64ProfileString == null)) 	throw (new ServletException("Error: Submitted code is invalid or missing."));
		
		try {
			bootstrapProfile.readBase64ProfileString(base64ProfileString);
		} catch (VariantDoesNotMatchAnAllowedVariantException e) {
			throw (new ServletException( e.getMessage()));
		} catch (BadFormedBase64NumberException e) {
			throw (new ServletException( e.getMessage()));
		}
		
		HashMap<String,ArrayList<String>> list_recommendations = bootstrapProfile.obtainDrugRecommendations();
		Map<String,StringBuffer> valuesMap = new HashMap<String, StringBuffer>();
		
		// Output raw data
		StringBuffer rawDataHTML = new StringBuffer("");
		if(list_recommendations.containsKey("raw_data")){
			rawDataHTML.append("<ul data-inset='true'>");
			ArrayList<String> list_data = list_recommendations.get("raw_data");
			Collections.sort(list_data);
			
			Iterator<String> it_data = list_data.iterator();
			while(it_data.hasNext()){
				rawDataHTML.append("<li>"+it_data.next()+"</li>");
			}
			rawDataHTML.append("</ul>");
			list_recommendations.remove("raw_data");
		}
		valuesMap.put("raw_data", rawDataHTML);
		
		
		// Output inferred alleles
		StringBuffer allelesHTML = new StringBuffer("");
		if(list_recommendations.containsKey("inferred_alleles")){
			allelesHTML.append("<ul data-inset='true'>");
			Iterator<String> it_data = list_recommendations.get("inferred_alleles").iterator();
			while(it_data.hasNext()){
				allelesHTML.append("<li>"+it_data.next()+"</li>");
			}
			allelesHTML.append("</ul>");
			list_recommendations.remove("inferred_alleles");
		}
		valuesMap.put("inferred_alleles", allelesHTML);
				
		
		// Output recommendations
		StringBuffer recommendationsHTML = new StringBuffer("");
		
		if(!list_recommendations.isEmpty()){
			recommendationsHTML.append("<ul data-role='listview' data-inset='true' data-filter-placeholder='Type medication name...' data-filter='true'>");
			Iterator<String> it_keys = list_recommendations.keySet().iterator();
			while(it_keys.hasNext()){
				String key = it_keys.next();
				String drug_recommendations = "<ul data-inset='true'>";
				Iterator<String> list_data = list_recommendations.get(key).iterator();
				while(list_data.hasNext()){
					drug_recommendations+="<li>"+list_data.next()+"</li>";
				}
				drug_recommendations+="</ul>";
				String new_drug = "<li><div data-filtertext=\""+key+"\" data-role=\"collapsible\"><h3>"+key+"</h3>"+drug_recommendations+"</div></li>";
				recommendationsHTML.append(new_drug);
			}
			recommendationsHTML.append("</ul>");
		}
		
		valuesMap.put("recommendations", recommendationsHTML);
		list_recommendations.clear();
		
		
		// Below, the Apache StrSubstitutor class is used as a very simple
		// templating engine		
		StringReader myStringReader = new StringReader();
		String templateString = myStringReader.readFile(path+"interpretation-template.html");
		StrSubstitutor sub = new StrSubstitutor(valuesMap);
		String resolvedString = sub.replace(templateString);

		out.println(resolvedString);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}