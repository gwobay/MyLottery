

public class MktQuote
{
public String symbol;
public String date;
public String time;
public String price;
public String vol;
public String bid;
public String bidSize;
public String ask;
public String askSize;
public String change;


	public String toString()
	{
		return symbol+"-"+date+"-"+time+"-"+price+"-"+vol+"-"+bid+"-"+bidSize+"-"+ask+"-"+askSize+"-"+change;
	}

	public MktQuote()
	{
	}

	public 	MktQuote(String symbol1, String date1,String time1,String price1,String vol1,
				String bid1,String bidSize1,String ask1,String askSize1)
	{
		symbol=symbol1;
		date=date1;
		time=time1;
		price=price1;
		vol=vol1;
		bid=bid1;
		bidSize=bidSize1;
		ask=ask1;
		askSize=askSize1;
	}

	public 	MktQuote(MktQuote b)
	{
		symbol=b.symbol;
		date=b.date;
		time=b.time;
		price=b.price;
		vol=b.vol;
		bid=b.bid;
		bidSize=b.bidSize;
		ask=b.ask;
		askSize=b.askSize;
		change=b.change;
	}
	
}

