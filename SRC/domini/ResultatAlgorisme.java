package domini;

//Classe concebuda com DTO (Data Transfer Object) per passar dades de forma facil entre les diferents classes de Domini.
public class ResultatAlgorisme {

	// ATRIBUTS

	// Contingut resultant del proces de compressio/descompressio d'un contingut inicial.
	private byte[] contingut;

	// Variable que conté l'algorisme utilitzant per dur a terme el proces de compressio/descompressio.
	private String algorisme;

	//Estadistiques Locals generades durant el proces de compressio/descompressio
	private EstadistiquesLocals estadistiques;

	// CREADORES

	public ResultatAlgorisme(){}

	// GETTERS I SETTERS

	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	
	public void setEstadistiques(EstadistiquesLocals estadistiques) {
		this.estadistiques = estadistiques;
	}
	
	public void setAlgorisme(String algorism) {
		this.algorisme = algorism;
	}
	
	public byte[] getContingut() {
		return this.contingut;
	}
	
	public EstadistiquesLocals getEstadistiques() {
		return this.estadistiques;
	}
	
	public String getAlgorisme() {
		return algorisme;
	}
}
