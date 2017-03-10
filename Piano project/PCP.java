
public class PCP {
	public final static double E = 2;
	public static double minLevel = 1000000.0;
	
	public double[] descripteur;
	public double level;
	
	public PCP(FourrierSample fSample) {
		descripteur = new double[12];
		level = 0;
		for (int l = 1; l < fSample.data.length / 2; l++) {
			int k = M(l, fSample.data.length, fSample.sampleRate);
			descripteur[k] += Math.pow(fSample.data[l], E);
			level += fSample.data[l];
		}
		double s = 0;
		for (int l = 0; l < 12; l++) {
			s += descripteur[l];
		}
		for (int l = 0; l < 12; l++) {
			descripteur[l] = descripteur[l] / s;
		}
		//compute();
	}
	
	private static int M(int l, int n, int fs) {
		if (l == 0)
			return -1;
		else {
			int t = ((int) Math.round(12 * Math.log((((double) (fs * l)) / (double) n) / 261.63) / Math.log(2.0))) % 12;
			if (t < 0)
				return t + 12;
			return t;
		}
	}
	
	public void compute() {
		/*
		if(level < minLevel) {
			for(int i = 0; i < 12; i++)
				this.descripteur[i] = 0;
			return;
		} */
		/*
		for(int i = 0; i < 12; i++)
			if(this.descripteur[i] < 0.2)
				this.descripteur[i] = 0.0;
			else
				this.descripteur[i] = 1.0;
				*/
				
		double variance = 0.0;
		double moyenne = 1.0/12.0;
		for(int i = 0; i < 12; i++) 
			variance += Math.pow(this.descripteur[i] - moyenne, 2);
		variance *= moyenne;
		for(int i = 0; i < 12; i++)
			if(this.descripteur[i] < moyenne + variance*2)
				this.descripteur[i] = 0;
	}
	
	public boolean[] toBool() {
		boolean[] s = new boolean[12];
		for(int i = 0; i < 12; i++)
			s[i] = (descripteur[i] > 0.1);
		return s;
	}
	
}
