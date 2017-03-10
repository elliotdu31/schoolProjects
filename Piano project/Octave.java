
public class Octave {
	int c_bémole;
	int c;
	int c_dièz;
	int d;
	int d_dièz;
	int e;
	int f;
	int f_dièz;
	int g;
	int g_dièz;
	int a;
	int a_dièz;
	int b;
	int b_dièz;

	public Octave(double fInitial) {
		double multiplicator = Math.pow(2.0,  1.0/12.0);
		
		this.c_bémole = (int)fInitial;
		fInitial *= multiplicator;
		this.c = (int)fInitial;
		fInitial *= multiplicator;
		this.c_dièz = (int)fInitial;
		fInitial *= multiplicator;
		this.d = (int)fInitial;
		fInitial *= multiplicator;
		this.d_dièz = (int)fInitial;
		fInitial *= multiplicator;
		this.e = (int)fInitial;
		fInitial *= multiplicator;
		this.f = (int)fInitial;
		fInitial *= multiplicator;
		this.f_dièz = (int)fInitial;
		fInitial *= multiplicator;
		this.g = (int)fInitial;
		fInitial *= multiplicator;
		this.g_dièz = (int)fInitial;
		fInitial *= multiplicator;
		this.a = (int)fInitial;
		fInitial *= multiplicator;
		this.a_dièz = (int)fInitial;
		fInitial *= multiplicator;
		this.b = (int)fInitial;
		fInitial *= multiplicator;
		this.b_dièz = (int)fInitial;
	}
}
