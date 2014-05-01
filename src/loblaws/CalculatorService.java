package loblaws;

import java.math.BigDecimal;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;

@Path("/calculator")
public class CalculatorService {

	@GET
	@Path("/evaluate/{equation}")
	public Response eval(@PathParam("equation") String equation) {
		try {
			String output = evaluateExp(equation);
			return Response.status(200).entity("" + output).build();
		} catch( Exception e ) {
			System.out.println(e.getMessage());
			return Response.serverError().build();
		}
	}
	
	//this method takes care of ^ in the input expression.
	public String evaluateExp(String expression) throws ScriptException{
		//remove all spaces
		expression.replaceAll("\\s+","");
		//find if there is ^
		int caretIndex = expression.indexOf('^');
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		//base case, will not come back to loop any more
		if(caretIndex<0){
			String output = engine.eval(expression).toString();

			return output;
		}
		//there is at least one ^, process the ^, and break up the expression
		else{			
			//find the x part of w+x^y+z, find x's start position
			int xStartIndex=caretIndex-1;
			for(int i = caretIndex-1; i>-1;i--){
				//loop until find first non digit char, then we found x start
				if(Character.isDigit(expression.charAt(i))){
					xStartIndex = i;
				}
				else{
					break;
				}
			}
			String xVar = expression.substring(xStartIndex,caretIndex);
			String wVar = expression.substring(0,xStartIndex);
			//then find y after the caret
			int yEndIndex=caretIndex+1;
			for(int i = caretIndex+1; i<expression.length();i++){
				//loop until find first non digit char, then we found y
				if(Character.isDigit(expression.charAt(i))){
					yEndIndex = i;
				}
				else{
					break;
				}
			}
			String yVar = expression.substring(caretIndex+1,yEndIndex+1);
			String zVar = expression.substring(yEndIndex+1);
			return evaluateExp(engine.eval(wVar + "Math.pow("+ xVar +","+yVar + ")"+zVar).toString());
		}
	}
}
