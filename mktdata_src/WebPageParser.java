import java.util.Vector;

public abstract class WebPageParser
{
public boolean MULTI_QUOTE_PARSER=false;
public String name;
protected String readWebPage;
abstract MktQuote parsePage(String readWebPage);
abstract Vector<MktQuote> parsePageMultiple(String readWebPage);
//abstract String priceChange();
	public String getData(String aToken)
	{
	
		int i0=aToken.indexOf('>');
		if (i0++ < 0) return "";
		int i1=aToken.indexOf('<', i0);
		if (i1 < 0) return "";
		return aToken.substring(i0, i1);
	}

	public String removeComma(String inS)
	{
	String outS=new String("");
	String[] toks=inS.split(",");
		for (int i=0; i<toks.length; i++)
		outS += toks[i];
	return outS;
	}

}

