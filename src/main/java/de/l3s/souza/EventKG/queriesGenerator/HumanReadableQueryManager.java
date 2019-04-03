package de.l3s.souza.EventKG.queriesGenerator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.l3s.souza.EventKG.queriesGenerator.nlp.Relation;


public class HumanReadableQueryManager {

		private Relation relation1 = new Relation();
		private Relation relation2 = new Relation();
		private Map<String,String> varsURIs = new HashMap<String,String>();
		
			public String getStringUrlFormat (String input, int queryId) throws UnsupportedEncodingException
			{
				String str = input;
				String output = "";
				StringTokenizer token = new StringTokenizer (str,"\n");
				String obj = "";
				String sub = "";
				String role = "";
				String linesTimeStamp = "";
				String linesFilter = "";
				ArrayList<String> vars = new ArrayList<String> ();
				
				String clauseQuery = "";
				int relations = 0;
				
				String lineLimit = "";
				
				output = output + "<p class=\"tab2\">" + "<b> Query " +queryId +"</b>" + "\n";
				while (token.hasMoreTokens())
				{
					String currentLine = token.nextToken();
					if (currentLine.contains("PREFIX"))
						continue;
					
					if (currentLine.contains("rdf:type"))
						continue;
					
					if (currentLine.contains("?place") && !(currentLine.contains("owl:sameAs")))
						continue;
					
					if (currentLine.contains("?nextEvent") && !(currentLine.contains("owl:sameAs")))
						continue;
					
					if (currentLine.contains("?previousEvent") && !(currentLine.contains("owl:sameAs")))
						continue;
					
					if (currentLine.contains("rdf:object"))
					{
						StringTokenizer tokenObj = new StringTokenizer (currentLine);
						obj = tokenObj.nextToken();
						obj = tokenObj.nextToken();
						obj = tokenObj.nextToken();
						
						if (relations==0)
							relation1.setObjectString(obj);
						else
							relation2.setObjectString(obj);
						continue;
					}
					
					if (currentLine.contains("rdf:subject"))
					{
						StringTokenizer tokenObj = new StringTokenizer (currentLine);
						sub = tokenObj.nextToken();
						sub = tokenObj.nextToken();
						sub = tokenObj.nextToken();
						
						if (relations==0)
							relation1.setSubjectString(sub);
						else
							relation2.setSubjectString(sub);
						
						continue;
					}
					
					if (currentLine.contains("sem:roleType"))
					{
						StringTokenizer tokenObj = new StringTokenizer (currentLine);
						role = tokenObj.nextToken();
						role = tokenObj.nextToken();
						role = tokenObj.nextToken();
						
						if (relations==0)
							relation1.setRoleType(role);
						else
							relation2.setRoleType(role);
						
							
						if (relations==0)
							output = output + "<p class=\"tab2\">" + "<b>" +sub +"</b>" + " " + role + " " + "<b>"+ obj +"</b>" + "\n";
						else
							output = output + "<p class=\"tab2\">" + "<b>" +sub +"</b>" + " " + role + " " + "<b>"+ obj +"</b>" + "<br /><br />\n";
						
						relations++;
						continue;
					}
					
					if (currentLine.contains("SELECT") || currentLine.contains("ASK"))
					{
						
						clauseQuery = "<p class=\"tab\"> " + currentLine + " <br />";
						if (currentLine.contains("SELECT"))
						{
							String modifiedLine = "";
							StringTokenizer tokenSelect = new StringTokenizer (currentLine);
							while (tokenSelect.hasMoreTokens())
							{
								String currentToken = tokenSelect.nextToken();
								if (currentToken.contains("?"))
								{
									if (!currentToken.contains("count"))
									{
										vars.add(currentToken);
										currentToken = "<b>" + currentToken + "</b>";
									}
								}
								modifiedLine = modifiedLine + currentToken;
							}
							
							output = "<p class=\"tab\">" + modifiedLine + "<br />";
						}
						output = "<p class=\"tab\">" + currentLine + "<br />";
					}
						
					else
					if ( !currentLine.contains("LIMIT") && currentLine.contains("owl:sameAs"))
					{
						currentLine = currentLine.replaceAll("<", "");
						currentLine = currentLine.replaceAll(">", "");
						String varName = "";
						String modifiedLineVars = "";
						StringTokenizer tokenVar = new StringTokenizer (currentLine);
						while (tokenVar.hasMoreTokens())
						{
							String currentTokenVar = tokenVar.nextToken();
							if (currentTokenVar.contains("?event")  || currentTokenVar.contains("?entity"))
							{
								varName = currentTokenVar;
								if (modifiedLineVars.isEmpty())
									modifiedLineVars = modifiedLineVars + "<b>" + currentTokenVar + "</b>";
								else
									modifiedLineVars = modifiedLineVars + " <b>" + currentTokenVar + "</b>";
								continue;
							}
							
							if (currentTokenVar.contains("http://"))
							{
								String abbreviation="";
								modifiedLineVars = modifiedLineVars + " <a href=\" " + currentTokenVar + "\" " + " target=\"_blank\">"+
								
								currentTokenVar+"</a>";
								
								
//								System.out.println(currentTokenVar);
								abbreviation = currentTokenVar.replaceAll("http://dbpedia.org/resource/", "");
//								System.out.println(abbreviation);
								if (relation1.getSubjectString().contentEquals(varName))
								{
									relation1.setSubjectString("<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>");
									varsURIs.put(varName,"<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>" );
							//		System.out.println(varName);
							//		System.out.println(varsURIs.get(varName));
								}
								else
									if (relation1.getObjectString().contentEquals(varName))
									{
										relation1.setObjectString("<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>");
										varsURIs.put(varName,"<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>" );
//										System.out.println(varName);
//										System.out.println(varsURIs.get(varName));
									}
									else
										if (relation2.getSubjectString().contentEquals(varName))
										{
											relation2.setSubjectString("<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>");
											varsURIs.put(varName,"<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>" );
//											System.out.println(varName);
//											System.out.println(varsURIs.get(varName));
										}
										else
											if (relation2.getObjectString().contentEquals(varName))
											{
												relation2.setObjectString("<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>");
												varsURIs.put(varName,"<a href=\"" + currentTokenVar + "\"" + " target=\"_blank\">"+abbreviation+"</a>" );
//												System.out.println(varName);
//												System.out.println(varsURIs.get(varName));
											}
								
								continue;
							}
							
							if (modifiedLineVars.isEmpty())
								modifiedLineVars = modifiedLineVars + currentTokenVar;
							else
								modifiedLineVars = modifiedLineVars +  " " +currentTokenVar;
						}	
						output = output + modifiedLineVars + "<br />\n";
					}
					else
						if (!currentLine.contains("LIMIT") && !currentLine.contains("FILTER") && !currentLine.contains("owl:sameAs"))
						{
							String modifiedLineVars = "";
							StringTokenizer tokenVar = new StringTokenizer (currentLine);
							linesTimeStamp = linesTimeStamp + " </p>\n <p class=\"tab2\"> " + currentLine + " <br />";
							
							while (tokenVar.hasMoreTokens())
							{
								String currentTokenVar = tokenVar.nextToken();
								if (currentTokenVar.contains("event") || currentTokenVar.contains("entity"))
								{
									if (modifiedLineVars.isEmpty())
										modifiedLineVars = modifiedLineVars + " <b> " + currentTokenVar + " </b> ";
									else
										modifiedLineVars = modifiedLineVars + " <b> " + currentTokenVar + " </b> ";
									continue;
								}
								
								if (modifiedLineVars.isEmpty())
									modifiedLineVars = modifiedLineVars + currentTokenVar;
								else
									modifiedLineVars = modifiedLineVars +  " " +currentTokenVar;
							}
							output = output + modifiedLineVars + "<br />\n";
						}
					if ( currentLine.contains ("FILTER") )
					{
						StringTokenizer tokenFilter = new StringTokenizer (currentLine);
						String simplifiedFilter = "";
						
						
						while (tokenFilter.hasMoreElements())
						{
							String currentTokenFilter = tokenFilter.nextToken();
							if (currentTokenFilter.contains("xsd:date"))
							{
								currentTokenFilter = currentTokenFilter.substring(1, 5);
								
								if (simplifiedFilter.isEmpty())
									simplifiedFilter = simplifiedFilter + currentTokenFilter;
								else
									simplifiedFilter = simplifiedFilter + " " + currentTokenFilter;
								continue;
								
							}
							
							if (simplifiedFilter.isEmpty())	
							
								simplifiedFilter = simplifiedFilter + currentTokenFilter;
							else
							{
								if (currentTokenFilter.contains("event"))
									simplifiedFilter = simplifiedFilter + " <b>" + currentTokenFilter + "</b>";
								else
									simplifiedFilter = simplifiedFilter + " " + currentTokenFilter;
							}
						}
						simplifiedFilter = simplifiedFilter + ")";
						linesFilter = linesFilter + " </p>\n <p class=\"tab2\"> " + simplifiedFilter + " <br />";
						output = output +"</p>\n <p class=\"tab2\">" + simplifiedFilter + "<br />\n";
					}
					
					if ( currentLine.contains ("LIMIT") )
					{
						if (clauseQuery.contains("count"))
						{
						//	lineLimit =  "<p class=\"tab\"> ORDER BY DESC (?count) <br />" + "<p class=\"tab\"> } <br />" ;
							lineLimit =  "<p class=\"tab\"> } <br />" ;
							
						}
						else
							lineLimit = "<p class=\"tab\">" + "} ";
						output = output +"</p>\n <p class=\"tab2\">" + currentLine + "<br />";
					}
					
				}
				
				
				output = output.replaceAll("}", "");
				
				if (!output.contains("SELECT"))
					output = output +"</p>\n";
			
				
				String lineRelation1 = "</p> \n<p class=\"tab2\"> " + relation1.getSubjectString() + " " + relation1.getRoleType() + " " 
						+ relation1.getObjectString() + " . <br />";
		//		System.out.println("Line relation 1 before: " + lineRelation1);			
				
				String lineRelation2 = "</p> \n<p class=\"tab2\"> " + relation2.getSubjectString() + " " + relation2.getRoleType() + " " 
						+ relation2.getObjectString() + " . <br />";
				
		//		System.out.println("Line relation 2 before: " + lineRelation2);	
				output = lineRelation1 + lineRelation2 + linesTimeStamp + linesFilter + lineLimit;
				
//				System.out.println("linesTimeStamp before: "  + linesTimeStamp + "------");
				
				if (!linesTimeStamp.contains("EndTimeStamp") && !linesTimeStamp.contains("BeginTimeStamp"))
					linesTimeStamp = "";
				
				if (!linesFilter.contains("FILTER"))
					linesFilter = "";
				
				linesTimeStamp = removeExtraCharsTimeStamps (linesTimeStamp);
				
				linesTimeStamp = setDbrVariables (linesTimeStamp);
				
//				System.out.println("linesTimeStamp after: "  + linesTimeStamp + "-----");
				
//				System.out.println("linesFilter before: "  + linesFilter + "------");
				linesFilter = setDbrVariables (linesFilter);
//				System.out.println("linesFilter after: "  + linesFilter + "------");
				
				lineRelation1 = setDbrVariables (lineRelation1);
//				System.out.println("Line relation 1 after: " + lineRelation1);

				lineRelation2 = setDbrVariables (lineRelation2);
//				System.out.println("Line relation 2 after: " + lineRelation2);
				
				if (clauseQuery.contains("ASK"))
				{
					clauseQuery = clauseQuery + "<p class=\"tab\"> {";
					lineLimit = "<p class=\"tab\"> }";
				}
				
				if (clauseQuery.contains("DISTINCT"))
					lineLimit = "<p class=\"tab\"> }";
				return clauseQuery + lineRelation1 + lineRelation2 + linesTimeStamp + linesFilter + lineLimit;
			}
			
			private String removeExtraCharsTimeStamps (String ts)
			{
				String newTs = "";
				StringTokenizer tokenTs = new StringTokenizer (ts,"\n");
				
				while (tokenTs.hasMoreTokens())
				{
					String currentLine = tokenTs.nextToken();
					if (currentLine.contains("sem:hasBeginTimeStamp") || currentLine.contains("sem:hasEndTimeStamp"))
					{
						newTs = newTs + currentLine + "\n";
					}
				}
				
				return newTs;
			}
			private String setDbrVariables (String line)
			{
				String newStr = "";
				
				StringTokenizer tokenLine = new StringTokenizer (line);
				
				if (tripleContainsVar(line))
				{
					while (tokenLine.hasMoreTokens())
					{
						String currentToken = tokenLine.nextToken();
						if (varsURIs.containsKey(currentToken))
						{
							if (newStr.isEmpty())
								newStr = newStr + varsURIs.get(currentToken);
							else
								newStr = newStr + " " + varsURIs.get(currentToken);
							
						}
						else
						{
							if (newStr.isEmpty())
							{
								if ((currentToken.contains("entity") || currentToken.contains("event")) && (!currentToken.contains("http") && !currentToken.contains("target")))
									newStr = newStr + "<b> " + currentToken + " </b>";
								else
									newStr = newStr + currentToken;
								
							
								
							}
							else
							{
								if ((currentToken.contains("entity") || currentToken.contains("event")) && (!currentToken.contains("http") && !currentToken.contains("target")))
									newStr = newStr + " " + "<b> " + currentToken + " </b>";
								else 
									newStr = newStr + " " + currentToken;
								
							}
						}
					}
					
				}
				else
				{
					while (tokenLine.hasMoreTokens())
					{
							String currentToken = tokenLine.nextToken();
						
							if (newStr.isEmpty())
							{
								if ((currentToken.contains("entity") || currentToken.contains("event"))  && (!currentToken.contains("http") && !currentToken.contains("target")))
									newStr = newStr + "<b> " + currentToken + " </b>";
								else
									newStr = newStr + currentToken;
							}
							else
							{
								if ((currentToken.contains("entity") || currentToken.contains("event"))  && (!currentToken.contains("http") && !currentToken.contains("target")))
									newStr = newStr + " " + "<b> " + currentToken + " </b>";
								else 
									newStr = newStr + " " + currentToken;
							}
						
					}
					return newStr;
				}
				
				return newStr;
			}
			
			private boolean tripleContainsVar (String triple)
			{
				
				StringTokenizer tokenTriple = new StringTokenizer (triple);
				
				while (tokenTriple.hasMoreTokens())
				{
					if (varsURIs.containsKey(tokenTriple.nextToken()))
							return true;
				}
				
				return false;
			}
			
			public String getStringUrlFormatNl (String input) throws UnsupportedEncodingException
			{
				String str = URLDecoder.decode(input, "UTF-8");
				String output = "<p># ";
				
				StringTokenizer token = new StringTokenizer (str,"\n");
				while (token.hasMoreTokens())
				{
					String currentLine = token.nextToken();
					
					if (currentLine.contains("NaturalLanguage"))
						continue;
					
					if (currentLine.contains("was the"))
						continue;
					
					if (currentLine.contains("filter in"))
						continue;
					
					if (currentLine.contains("entity") || currentLine.contains("event"))
					{
						if (output.contentEquals("<p># "))
						{
							output = output + currentLine  + "<br />\n";
						
						}	
						else
						{
							output = output + "# "+currentLine  + "<br />\n";
						}
					}
				}
				output = output +"</p>\n";
				return output;
			}
			
			
			public String getHtmlHead ()
			{
				return ( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" + 
						"<html>\n" + 
						"<head>\n" + 
						 "<meta charset=\"UTF-8\">" +
						"\n" + 
						"<style type=\"text/css\">\n" + 
						"a\n" + 
						"{\n" + 
						"target-name:new;\n" + 
						"target-new:window;\n" + 
						"}\n" + 
						".tab { margin-left: 40px; }\n" + 
						".tab2 { margin-left: 80px; }\n" + 
						".noBorder {\n" + 
						"    border:none !important;\n" + 
						"}\n" + 
						"table {\n" + 
						"    border-collapse: collapse;\n" + 
						"    width: 100%;\n" + 
						"}\n" + 
						"\n" + 
						"th, td {\n" + 
						"    text-align: left;\n" + 
						"    padding: 8px;\n" + 
						"}\n" + 
						"\n" + 
						"tr:nth-child(even) {background-color: #f2f2f2;}\n" + 
						"</style>" + "</head>\n" + 
								"<body>");
			}
			
			
			public String getHtmlEnd ()
			{
				return ("</body>\n" + 
						"</html>");
				
			}
	}


