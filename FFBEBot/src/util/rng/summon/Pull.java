package util.rng.summon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Library.summon.Awakening;
import Library.summon.Unit;
import Library.summon.UnitSpecific;
import Library.summon.banner.Banner;
import Library.summon.banner.BannerType;
import global.record.Log;
import util.rng.RandomLibs;

public class Pull {
	public static List<UnitSpecific> pull(int times,Banner banner){
		Random rand=new Random();
		ArrayList<UnitSpecific> units=new ArrayList<UnitSpecific>();
		for(int i=0;i<times;i++){
			try{
			int rarity=rand.nextInt(Arrays.stream(banner.type.raritySplit).sum());
			//5*
			if(rarity<banner.type.raritySplit[0]){
				Unit u=pull(banner,5,banner.type.raritySplit[0]*100,banner.type.baseRareChances[2]);
				units.add(new UnitSpecific(u,5));
			}
			//4*
			else if (rarity < banner.type.raritySplit[1]+banner.type.raritySplit[0]) {
				Unit u=pull(banner,4,banner.type.raritySplit[1]*100,banner.type.baseRareChances[1]);
				units.add(new UnitSpecific(u,4));
			}
			//3*
			else{
				Unit u=pull(banner,3,banner.type.raritySplit[2]*100,banner.type.baseRareChances[0]);
				units.add(new UnitSpecific(u,3));
			}
			}catch(Exception e){
				Log.logError(e);
			}
		}
		return units;
	}
	public static List<UnitSpecific> pull11(int times, Banner banner){
		ArrayList<UnitSpecific> units=new ArrayList<UnitSpecific>();
		for(int i=0;i<times;i++){
			units.addAll(pull11(banner));
		}
		return units;
	}
	public static List<UnitSpecific> pull11(Banner banner){
		ArrayList<UnitSpecific> units=new ArrayList<UnitSpecific>(11);
		Random rand=new Random();
		int rarity=rand.nextInt(100);
		try{
			if(rarity<94){
				Unit u=pullPlus(banner,4,banner.type.raritySplit[1]*100,banner.type.baseRareChances[1]);
				units.add(new UnitSpecific(u,4));
			}
			else{
				Unit u=pullPlus(banner,5,banner.type.raritySplit[0]*100,banner.type.baseRareChances[2]);
				units.add(new UnitSpecific(u,5));
			}
			for(int i=0;i<10;i++){
				rarity=rand.nextInt(99);
				//5*
				if(rarity<=banner.type.raritySplit[0]){
					Unit u=pull(banner,5,banner.type.raritySplit[0]*100,banner.type.baseRareChances[2]);
					units.add(new UnitSpecific(u,5));
				}
				//4*
				else if(rarity <= banner.type.raritySplit[1]+banner.type.raritySplit[0]){
					Unit u=pull(banner,4,banner.type.raritySplit[1]*100,banner.type.baseRareChances[1]);
					units.add(new UnitSpecific(u,4));
				}
				//3*
				else{
					Unit u=pull(banner,3,banner.type.raritySplit[2]*100,banner.type.baseRareChances[0]);
					units.add(new UnitSpecific(u,3));
				}
			}
		}catch(Exception e){
			Log.logError(e);
		}
		return units;
	}
	
	public static List<UnitSpecific> pull5base(int times,Banner banner){
		ArrayList<UnitSpecific> units=new ArrayList<UnitSpecific>(11);
		for(int i=0;i<times;i++){
			try{
				Unit u=pull(banner,5,banner.type.raritySplit[0]*100,banner.type.baseRareChances[2]);
				units.add(new UnitSpecific(u,5));
			}catch(Exception e){
				Log.logError(e);
			}
		}
		return units;
	}
	/**
	 * Pulls a unit from banner
	 * @param banner banner to pull from
	 * @param rarity rarity of unit
	 * @param poolSize the int size of the pool / % of total pool it takes up i.e x/10000
	 * @param baseChance chance for unit to be the base rarity
	 * @return unit chosen
	 */
	private static Unit pull(Banner banner,int rarity,int poolSize,int baseChance){
		Unit[] pool;//used to construct pool
		Random rand=new Random();
		int select=rand.nextInt(100);//determine if base rarity or not and pull from that pool
		if(select<baseChance){
			pool=baseRarity(banner,rarity);
		}
		else{
			pool=hasRarity(banner,rarity);
		}
		int next=rand.nextInt(poolSize);//gets chosen int for rarity pool to determine if featured unit or not
		int[] featured=new int[banner.featured.length];//for featured units at the rarity
		int i=0;
		for(Unit u:banner.featured){//determine for each if 
			if(u.hasRarity(rarity)){//if it has rarity/upgrade featured at this rarity
				featured[i]=banner.percent[i][u.getRarityIndex(rarity)];
			}
			else if(u.hasUpgrade(rarity, banner.include)){
				featured[i]=banner.percent[i][u.getUpgradeIndex(rarity, banner.include)];
			}
			else{//otherwise no chance
				featured[i]=0;
			}
			i++;
		}
		i=0;
		for(int c=0;c<featured.length;c++){//determine if number is within range of one of the featured units
			if(next<featured[c]+i&&next>=i){
				return banner.featured[c];
			}
			i+=featured[c];
		}
		return RandomLibs.SelectRandom(pool);//otherwise select a random unit from the pool
	}
	//boosted rate of featrued for banner 3.75 for 5*, 47.5% for 4*
	private static Unit pullPlus(Banner banner,int rarity,int poolSize,int baseChance){
		if(rarity<4){
			throw new IllegalArgumentException("The bonus crystal must be 4* or higher");
		}
		Unit[] pool;//used to construct pool
		Random rand = new Random();
		int select = rand.nextInt(100);
		if(select<baseChance){
			pool=baseRarity(banner,rarity);
		}
		else{
			pool=hasRarity(banner,rarity);
		}
		int next;
		if((banner.type==BannerType.ThreePercent||banner.type==BannerType.FivePercent)
			&&(rarity==4||rarity==5)){
			//increased rate for featured update
			next = rand.nextInt(10000);
			if(rarity==4){
				if(next<4750){
					ArrayList<Unit> units = new ArrayList<Unit>();
					for(Unit u:banner.featured){
						if(u.baseRarity()==4){
							units.add(u);
						}
					}
					if(units.size()>0){
						return RandomLibs.SelectRandom(units);
					}
				}
			}
			else if(rarity==5){
				if(next<375||banner.type.featuredOnly){
					ArrayList<Unit> units = new ArrayList<Unit>();
					for(Unit u:banner.featured){
						if(u.baseRarity()==5){
							units.add(u);
						}
					}
					if(units.size()>0){
						return RandomLibs.SelectRandom(units);
					}
				}
			}
		}
		else{
			next =rand.nextInt(poolSize);//gets chosen int for rarity pool to determine if featured unit or not
			int[] featured=new int[banner.featured.length];//for featured units at the rarity
			int i=0;
			for(Unit u:banner.featured){//determine for each if 
				if(u.hasRarity(rarity)){//if it has rarity/upgrade featured at this rarity
					featured[i]=banner.percent[i][u.getRarityIndex(rarity)];
				}
				else if(u.hasUpgrade(rarity, banner.include)){
					featured[i]=banner.percent[i][u.getUpgradeIndex(rarity, banner.include)];
				}
				else{//otherwise no chance
					featured[i]=0;
				}
				i++;
			}
			i=0;
			for(int c=0;c<featured.length;c++){//determine if number is within range of one of the featured units
				if(next<featured[c]+i&&next>=i){
					return banner.featured[c];
				}
				i+=featured[c];
			}
		}
		return RandomLibs.SelectRandom(pool);//otherwise select a random unit from the pool
	}
	/**
	 * gets units with this rarity in the banner
	 * @param banner banner to look in
	 * @param rarity rarity
	 * @return array of units that have this rarity
	 */
	private static Unit[] hasRarity(Banner banner,int rarity){
		ArrayList<Unit> units=new ArrayList<Unit>();
		for(Unit u:banner.getPool()){//gets units in banner pool and does check if it has rarity
			if(u.hasRarity(rarity)){
				units.add(u);//if yes add to list
			}
		}
		if(!(banner.include==null)){
			for(Awakening a:banner.include){//does the same for upgrades, if the banner has it
				for(int i=0;i<a.units.length;i++){
					if(a.units[i].hasUpgrade(rarity, a.rarityAwakened[i])){
						units.add(a.units[i]);
					}
				}
			}
		}
		return toArray(units);
	}
	/**
	 * gets units with this rarity as their base rarity
	 * @param banner banner to look in
	 * @param rarity rarity
	 * @return array of units whose base rarity is this rarity
	 */
	private static Unit[] baseRarity(Banner banner,int rarity){
		ArrayList<Unit> units=new ArrayList<Unit>();
		for(Unit u:banner.getPool()){//gets units in banner pool and does check if it is this base rarity
			if(u.base==rarity){
				units.add(u);//if yes add to list
			}
		}
		return toArray(units);
	}
	/**
	 * conversion from arraylist to array
	 * @param units arraylist to convert
	 * @return array of the units
	 */
	private static Unit[] toArray(ArrayList<Unit> units){
		Unit[] unit=new Unit[units.size()];
		for(int i=0;i<units.size();i++){
			unit[i]=units.get(i);
		}
		return unit;
	}
}
