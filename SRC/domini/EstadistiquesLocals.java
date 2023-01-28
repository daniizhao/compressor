package domini;

public class EstadistiquesLocals {

	// ATRIBUTS CLASSE
	private float temps;
	private float mida_abans;
	private float mida_despres;
	private boolean compressio;
	
	// CONSTRUCTORES
	public EstadistiquesLocals() {
		this.temps = 0;
		this.mida_abans = 0;
		this.mida_despres = 0;
		this.compressio = false;
	}
	
	public EstadistiquesLocals(float temps, float mida_abans, float mida_despres, boolean compressio) {
		this.temps = temps;
		this.mida_abans = mida_abans;
		this.mida_despres = mida_despres;
		this.compressio = compressio;
	}
	
	
	// METODES
	
	// GETTERS
	public float getTemps() {
		return temps;
	}
	
	public float getMidaAbans() {
		return mida_abans;
	}
	
	public float getMidaDespres() {
		return mida_despres;
	}
	
	public boolean esCompressio() {
		return compressio;
	}
	
	// SETTERS
	public void setTemps(float t) {
		this.temps =t;
	}
	
	public void setMidaAbans(float mA) {
		this.mida_abans = mA;
	}
	
	public void setMidaDespres(float mD) {
		this.mida_despres = mD;
	}
	
	public void setEsCompressio(boolean c) {
		this.compressio = c;
	}
	
	// Funcio per calcular la velocitat
	public float getVelocitat() {
		return mida_abans/ (temps/1000);
	}

	// Funcio per calcular el grau de compressio
	public float getGrauComp() {
		if (mida_abans == 0) mida_abans = 1;
		if (mida_despres == 0) mida_despres = 1;
		return (1-(mida_despres/mida_abans))*100;
	}
	
}