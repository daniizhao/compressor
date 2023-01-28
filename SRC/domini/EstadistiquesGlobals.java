package domini;

public class EstadistiquesGlobals {
	// ATRIBUTS CLASSE
	private float sum_velocitat_comp;	
	private float sum_velocitat_descomp;
	private float ntotal_comp;
	private float ntotal_descomp;
	private float sum_grau_comp;
	
	// CONSTRUCTORES
	public EstadistiquesGlobals() {
		this.sum_velocitat_comp = 0;
		this.sum_velocitat_descomp = 0;
		this.ntotal_comp = 0;
		this.ntotal_descomp = 0;
		this.sum_grau_comp = 0;
	}
	
	public EstadistiquesGlobals(float sumVC, float sumVD, float numC, float numD, float sumGC) {
		this.sum_velocitat_comp = sumVC;
		this.sum_velocitat_descomp = sumVD;
		this.ntotal_comp = numC;
		this.ntotal_descomp = numD;
		this.sum_grau_comp = sumGC;
	}
	
	// GETTERS
	public float getSumVelComp() {
		return sum_velocitat_comp;
	}
	
	public float getSumVelDescomp() {
		return sum_velocitat_descomp;
	}
	
	public float getNumTotalComp() {
		return ntotal_comp;
	}
	
	public float getNumTotalDescomp() {
		return ntotal_descomp;
	}

	public float getSumGrauComp() {
		return sum_grau_comp;
	}

	// Funcio per calcular la velocitat mitjana de compressio
	public float getVelMitjanaCompressio() {
		if(ntotal_comp == 0) return 0;
		else return sum_velocitat_comp/ntotal_comp;
	}

	// Funcio per calcular la velocitat mitjana de descompressio
	public float getVelMitjanaDescompressio() {
		if(ntotal_descomp == 0) return 0;
		else return sum_velocitat_descomp/ntotal_descomp;
	}

	// Funcio per calcular el grau mitja de compressio
	public float getGrauComp() {
		if(ntotal_comp == 0) return 0;
		else return sum_grau_comp/ntotal_comp;
	}
	
	// SETTERS
	public void setSumVelComp(float sumaVC) {
		this.sum_velocitat_comp = sumaVC;
	}
	
	public void setSumVelDescomp(float sumaVD) {
		this.sum_velocitat_descomp = sumaVD;
	}
	
	public void setNumTotalComp(float nbC) {
		this.ntotal_comp = nbC;
	}
	
	public void setNumTotalDescomp(float nbD) {
		this.ntotal_descomp = nbD;
	}

	public void setSumGrauComp(float sumaGrau) { this.sum_grau_comp = sumaGrau; }
	
	// Funcio per actualitzar estadistiques de l'algorisme
	public void actualitzaEstadistiques(EstadistiquesLocals est){
		if(est.esCompressio()) {
			sum_velocitat_comp += est.getVelocitat();
			ntotal_comp++;
			sum_grau_comp += est.getGrauComp();
		}
		else {
			sum_velocitat_descomp += est.getVelocitat();
			ntotal_descomp++;
		}
	}
	
}
