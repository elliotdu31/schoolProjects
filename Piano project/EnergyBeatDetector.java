import java.util.LinkedList;

public class EnergyBeatDetector {
	
	private LinkedList<SoundSample> sound;
	public EnergyBeatDetector(LinkedList<SoundSample> sound) {
		// TODO Auto-generated constructor stub
		this.sound=sound;
	}
	public LinkedList<Double> Energy(){
		LinkedList<Double> energy= new LinkedList<Double>();
		for (SoundSample i: this.sound) {
			double e=0;
			for (int j = 0; j < i.data.length; j++) {
				e=e+Math.pow(i.data[j],2);
			}
			energy.add(e);
		}
		for (Double double1 : energy) {
			double1=double1/energy.getLast();
		}
		return energy;
	}
	public double average(LinkedList<Double> Energy){
		double avg=0; int count=0;
		for (Double i: Energy) {
			count++;
			avg+= i;
		}
		avg=avg/count;
		return avg;
	}
	public double var(LinkedList<Double> Energy, double avg){
		double var =0;int count=0;
		for (Double i: Energy) {
			count++;
			var = var + Math.pow(avg-i, 2);
		}
		
		var=  var/count;
		return var;
		
	}
	public double treshhold(double var, double c1, double c2){
		return c2-c1*var;
	}
	public boolean isattack(){
		LinkedList<Double> Energy = this.Energy();
		double avg = this.average(Energy);
		//double treshhold = this.treshhold(this.var(Energy, avg), 0.000000000000000000015, 2.0);
		double treshhold = this.treshhold(this.var(Energy, avg), 0.0000000000000000001, 1.5);
		treshhold=treshhold*avg;
		if (Energy.getLast()> treshhold) 
				return true;
		return false;
	}
	public static boolean isattack(LinkedList<SoundSample> sound){
		EnergyBeatDetector list =new EnergyBeatDetector(sound);
		return list.isattack();
	}
}
