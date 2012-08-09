import java.util.ArrayList;

public class ClusterHelper {
	private double tolerance;
	private int minAmount;
	private ArrayList<Defect> orinDefs;
	private ArrayList<Defect> grpdDefs;
	
	private int nowGroups = 0;

	private double tolerance2;
	
	public ClusterHelper(double d, int i) {
		tolerance = d;
		minAmount = i;
		tolerance2 = tolerance * tolerance;
		grpdDefs = new ArrayList<Defect>();
	}
	
	public void setDefects(ArrayList<Defect> arrayList){
		orinDefs = arrayList;
	}

	public ArrayList<Defect> getDefects(){
		return grpdDefs;
	}
	
	public void cluster(){
		ArrayList<Defect> tempGroup;
		Defect thisDefect;
		
		while(orinDefs.size()>0){
			thisDefect = (Defect)orinDefs.get(0);				
			tempGroup = markGroup(thisDefect);

			if(tempGroup.size()>= minAmount){
				nowGroups++;			
				for(int i = 0;i < tempGroup.size(); i++){
					((Defect)tempGroup.get(i)).setClusterNo(nowGroups);						
				}					
			}
			else{
				for(int i = 0;i < tempGroup.size(); i++){
					((Defect)tempGroup.get(i)).setClusterNo(-1);
				}					
			}
			grpdDefs.addAll(tempGroup);
		}
	}
	
	private ArrayList<Defect> markGroup(Defect thisDefect){		
		ArrayList<Defect> thisGroup = new ArrayList<Defect>();
		
		thisGroup.add(thisDefect);	
			
		orinDefs.remove(thisDefect);		
		
		ArrayList<Defect> tempGroup = getNestestDefects(orinDefs, thisDefect);
		
		orinDefs.removeAll(tempGroup);
		
		for(int i = 0; i < tempGroup.size();i++){
			thisGroup.addAll(markGroup((Defect)tempGroup.get(i)));
		}
		
		return thisGroup;
	}
	
	public ArrayList<Defect> getNestestDefects(ArrayList<Defect> group, Defect defect){
		ArrayList<Defect> defects = new ArrayList<Defect>();
		double x1 = defect.getX();
		double y1 = defect.getY();
		Defect thatDefect;
		double x2, y2;
		for(int i = 0; i < group.size();i++){
			thatDefect = (Defect)group.get(i);
			//if(thatDefect.getClusterNo()==-1)continue;
			//System.out.println(thatDefect.getClusterNo());
			x2 = thatDefect.getX();
			y2 = thatDefect.getY();

			if((Math.pow(x2-x1,2)+Math.pow(y2-y1,2)) <= tolerance2){
				defects.add(thatDefect);
			}				
		}		
		return defects;
	}

	public int getCounts() {
		return minAmount;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setCounts(int i) {
		minAmount = i;
	}

	public void setTolerance(double d) {
		tolerance = d;
	}
	
	public void showtClusterNo(){
		for(int i = 0; i < grpdDefs.size();i++){
			System.out.print(((Defect)grpdDefs.get(i)).getClusterNo()+"  ");
			System.out.print("X:"+((Defect)grpdDefs.get(i)).getX()+" ");
			System.out.println("Y:"+((Defect)grpdDefs.get(i)).getY()+" ");

		}
	}
	
	public static void main(String[] args) {
		ClusterHelper myCH = new ClusterHelper(9.0 , 1);
		ArrayList<Defect> testAL = new ArrayList<Defect>();
		testAL.add(new Defect(10,10));
		testAL.add(new Defect(1,1));
		testAL.add(new Defect(23,23));
		testAL.add(new Defect(13,13));
		
		testAL.add(new Defect(11,11));
		testAL.add(new Defect(2,2));
		testAL.add(new Defect(20,20));
		testAL.add(new Defect(22,22));
		myCH.setDefects(testAL);
		myCH.cluster();		
		myCH.showtClusterNo();
	}
}
