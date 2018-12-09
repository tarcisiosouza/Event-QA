package de.l3s.souza.EventKG.queriesGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import de.l3s.souza.EventKG.queriesGenerator.nlp.Relation;

public class RandomFilter {

	private ArrayList<String> period;
	private ArrayList<String> popEvents;
	private ArrayList<String> place;
	private ArrayList<String> eventDecision;
	
	public RandomFilter() {
		
		period = new ArrayList<String> ();
		popEvents = new ArrayList<String> ();
		place = new ArrayList<String> ();
		eventDecision = new ArrayList<String> ();
		period.add("before");
		period.add("sameStartTime");
		period.add("sameRange");
		period.add("after");
		period.add("last");
		period.add("year");
		popEvents.add("pop");
		popEvents.add("notpop");
		place.add("same");
		place.add("different");
		eventDecision.add("specific");
		eventDecision.add("overall");
	}

	public String SelectPeriod ()
	{
		Random randPeriod = new Random (System.nanoTime());
		int positionPeriod = randPeriod.nextInt(period.size());
		return period.get(positionPeriod);
	}
	
	public String SelectPopEvents() {
		Random randPeriod = new Random (System.nanoTime());
		int pos = randPeriod.nextInt(popEvents.size());
		return popEvents.get(pos);
	}
	
	public String SelectPlace() {
		Random randPeriod = new Random (System.nanoTime());
		int pos = randPeriod.nextInt(place.size());
		return place.get(pos);
	}
	
	public String SelectEventDecision() {
		Random randPeriod = new Random (System.nanoTime());
		int pos = randPeriod.nextInt(eventDecision.size());
		return eventDecision.get(pos);
	}
	
	public String getRandomValue (ArrayList<String> array)
	{
		Random randArray = new Random (System.nanoTime());
		int pos = randArray.nextInt(array.size());
		return array.get(pos);
	}
	
	public String getRandomValue (Collection<String> array)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		Iterator<String> iter;

		Random randArray = new Random (System.nanoTime());
		int pos = randArray.nextInt(array.size());
		iter = array.iterator();
		
		while (iter.hasNext())
			arrayList.add(iter.next());
		
		return arrayList.get(pos);
	}
	
	public Relation getRandomValue (Map<String,Relation> map)
	{
		ArrayList<String> keys = new ArrayList<>(map.keySet());

		Random randArray = new Random (System.nanoTime());
		int pos = randArray.nextInt(keys.size());
		String key = keys.get(pos);
		
		Relation relation = map.get(key);
		return relation;
	}
	
	
}
