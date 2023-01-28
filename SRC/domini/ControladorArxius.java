package domini;

import dades.ControladorDades;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControladorArxius {
	// ATRIBUTS
	// Atributs per poder establir parametres sobre la compressio en JPEG
	private boolean mostreig = false;
	private boolean quant = true;

	// GETTERS I SETTERS

	public void setMostreig(boolean mostreig) {
		this.mostreig = mostreig;
	}

	public void setQuantitzacio(boolean quant){
		this.quant = quant;
	}

	// Configuracio per defecte de JPEG
	private void configuracioPerDefecteJPEG() {
		mostreig = false;
		quant = true;
	}

	// CONSTRUCTORA

	public ControladorArxius(){}

	// FUNCIONS D'ACCES A CAPA DE DADES

	public byte[] getContingutFitxer(String path) throws IOException {
		return dades.ControladorDades.getContingutFitxer(path);
	}

	public String getNomFitxer(String text) {
		return ControladorDades.getNomFitxer(text);
	}

	private Fitxer getFitxer(String path) throws IOException {
		return new Fitxer(dades.ControladorDades.getNomFitxer(path), path, dades.ControladorDades.getExtensioFitxer(path), dades.ControladorDades.getContingutFitxer(path), dades.ControladorDades.getContingutFitxer(path).length, true);
	}

	private void guardarFitxer(Fitxer f) throws IOException {
		dades.ControladorDades.creaFitxer(f.getNom(), f.getPath(), f.getExtensio(), f.getContingut());
	}

	// FUNCIONS PER COMPRIMIR FITXERS

	// Funcio publica per comprimir fitxers.
	public List<String> comprimir(String path_input, String path_output, String nom_algorisme, String nom_sortida) throws IOException {
		List<String> resultat = new ArrayList<>();	// Array per retornar el resultat de descomprimir.

		Fitxer f = getFitxer(path_input);
		ResultatAlgorisme result = comprimirComu(f.getContingut(), nom_algorisme,  f.getExtensio());
		// perque s'assigni l'extensio correcta quan l'algorisme es automatic
		String extensioResult = result.getAlgorisme();
		if (extensioResult.equals("jpeg")) extensioResult = "imcomp";

		Fitxer fc = new Fitxer(nom_sortida, path_output, extensioResult, result.getContingut(), result.getContingut().length, true);
		guardarFitxer(fc);

		resultat.add(Arrays.toString(result.getContingut()));
		resultat.add(result.getAlgorisme());
		resultat.add(String.valueOf(result.getEstadistiques().getTemps()));
		resultat.add(String.valueOf(result.getEstadistiques().getVelocitat()));
		resultat.add(String.valueOf(result.getEstadistiques().getGrauComp()));
		return resultat;	//result.getEstadistiques();
	}

	// Funcio comuna que s'utilitza tant a la compressio de fitxers i de carpetes per realitzar la compressio de fitxers simples.
	private ResultatAlgorisme comprimirComu(byte[] contingut, String nom_algorisme, String extensio)throws IOException {
		Algorisme a;
		ResultatAlgorisme result;

		switch (nom_algorisme) {
			case "lz78" :
				if(!extensio.equals("txt")) throw new IOException("No es pot comprimir aquest fitxer amb l'algorisme LZ78.");
				a = Algorisme.getInstance(nom_algorisme);
				result = a.comprimir(contingut);
				result.setAlgorisme(nom_algorisme);
				break;

			case "lzss" :
				if(!extensio.equals("txt")) throw new IOException("No es pot comprimir aquest fitxer amb l'algorisme LZSS.");
				a = Algorisme.getInstance(nom_algorisme);
				result = a.comprimir(contingut);
				result.setAlgorisme(nom_algorisme);
				break;

			case "lzw" :
				if(!extensio.equals("txt")) throw new IOException("No es pot comprimir aquest fitxer amb l'algorisme LZW.");
				a = Algorisme.getInstance(nom_algorisme);
				result = a.comprimir(contingut);
				result.setAlgorisme(nom_algorisme);
				break;

			case "jpeg" :
				if(!extensio.equals("ppm")) throw new IOException("No es pot comprimir aquest fitxer amb l'algorisme JPEG.");
				a = Algorisme.getInstance(nom_algorisme);
				JPEG j = (JPEG) a;

				// Seleccionam els parametres que l'usuari vol per comprimir la imatge.
				j.setMostreig(mostreig);
				j.setQuant(quant);
				result = a.comprimir(contingut);
				result.setAlgorisme(nom_algorisme);

				// A l'acabar, fiquem els parametres per defecte.
				configuracioPerDefecteJPEG();
				break;

			default : //Si s'ha utilitzat el metode de seleccio automatica.
				switch (extensio) {

					case "txt" :
						if (contingut.length < (75*1024)){ // mida del contingut < 75KB
							result = Algorisme.getInstance("lzss").comprimir(contingut);
							result.setAlgorisme("lzss");

						}
						else {
							result = Algorisme.getInstance("lzw").comprimir(contingut);
							result.setAlgorisme("lzw");
						}

						break;

					case "ppm"://Si es un .ppm utilitzarem l'algorisme jpeg
						result = Algorisme.getInstance("jpeg").comprimir(contingut);
						result.setAlgorisme("jpeg");
						break;

					default:
						throw new IOException("Extensio del fitxer no valida.");
				}

				break;
		}
		return result;
	}


	// FUNCIONS PER DESCOMPRIMIR FITXERS

	// Funcio pubica per descomprimir fitxers simples.
	public List<String> descomprimir(String path_input, String path_output, String nom_sortida) throws IOException {
		List<String> resultat = new ArrayList<>();	//Array per retornar el resultat de descomprimir.
		Fitxer f = getFitxer(path_input);

		ResultatAlgorisme result = descomprimirComu(f.getContingut(), f.getExtensio());

		String extensio;
		if(f.getExtensio().equals("imcomp")) {
			extensio = "ppm";
		} else {
			extensio = "txt";
		}

		Fitxer fitxerDesomprimit = new Fitxer(nom_sortida, path_output, extensio, result.getContingut(), result.getContingut().length, true);
		guardarFitxer(fitxerDesomprimit);

		resultat.add(Arrays.toString(result.getContingut()));
		resultat.add(result.getAlgorisme());
		resultat.add(String.valueOf(result.getEstadistiques().getTemps()));
		resultat.add(String.valueOf(result.getEstadistiques().getVelocitat()));
		return resultat;
	}


	// Funcio comuna que s'utilitza tant a la descompressio de fitxers com de carpetes per realitzar la descompressio de fitxers simples.
	private ResultatAlgorisme descomprimirComu(byte[] contingut, String algorisme) throws IOException {
		Algorisme a;
		ResultatAlgorisme result = new ResultatAlgorisme();

		if (algorisme.equals("imcomp")){
			algorisme = "jpeg";
		}

		switch (algorisme) {
			case "lz78" :
			case "lzss" :
			case "lzw" :
			case "jpeg" :

				result.setAlgorisme(algorisme);
				a = Algorisme.getInstance(algorisme);
				break;

			default:
				throw new IOException("No s'ha obtingut un algorisme valid.");

		}

		result = a.descomprimir(contingut);

		result.setAlgorisme(algorisme);

		return result;
	}


	// FUNCIO PER COMPARAR UN FITXER

	public List<byte[]> comparar(String path_input, String nom_algorisme) throws IOException {
		List<byte[]>  result = new ArrayList<>();
		Fitxer f = getFitxer(path_input);
		ResultatAlgorisme resultComp = comprimirComu(f.getContingut(), nom_algorisme, f.getExtensio());
		ResultatAlgorisme resultDescomp = descomprimirComu(resultComp.getContingut(), resultComp.getAlgorisme());

		result.add(f.getContingut());
		result.add(resultDescomp.getContingut());

		return result;
	}


	// FUNCIONS IMPLEMENTAR LA COMPRESSIO I DESCOMPRESSIO DE CARPETES.

	//funcio que s'utilitza per comprimir carpetes i que crida a una altra funcio per realitzar aquesta compressio
	public void comprimirCarpeta(String input_path, String output_path, String algorisme, String nom_sortida) throws IOException{
		ResultatAlgorisme result = comprimirCarpeta_aux(input_path, output_path, algorisme);
		Fitxer fitxer = new Fitxer(nom_sortida, output_path, "ccomp", result.getContingut(), result.getContingut().length, true);
		guardarFitxer(fitxer);
	}

	//funcio que comprimeix una carpeta i retorna un Resultat Algorisme amb el contingut de la carpeta comprimida
	private ResultatAlgorisme comprimirCarpeta_aux(String input_path, String output_path, String algorisme) throws IOException{

		ResultatAlgorisme result = new ResultatAlgorisme();
		// Retorna tots els paths dels fitxers de la carpeta: apartir d'aquests paths puc agafar tot del fitxer
		String[] path_fitxers = ControladorDades.getFitxers(input_path);

		ArrayList<Byte> header = new ArrayList<>();

		// indiquem que es una carpeta amb un 'c'
		header.add((byte)'c');

		// mida nom carpeta
		String nom_carpeta = ControladorDades.getNomCarpeta(input_path);
		header.add((byte)(char)nom_carpeta.length());

		// nom carpeta
		for(int i = 0; i<nom_carpeta.length(); ++i) header.add((byte)nom_carpeta.charAt(i));

		// Nombre de fitxers de la carpeta
		int num_chars_contingut = path_fitxers.length;
		byte[] bytes_num_contingut = ByteBuffer.allocate(4).putInt(num_chars_contingut).array();
		for (int j = 0; j< 4; ++j) header.add(bytes_num_contingut[j]);

		// iniciem el resultat amb la capçalera de la carpeta
		byte[] head = new byte[header.size()];
		for(int i = 0; i<header.size(); ++i) head[i] = header.get(i);
		result.setContingut(head);

		for ( int i = 0; i < path_fitxers.length; ++i ) {
			if ( ControladorDades.esCarpeta(input_path + File.separator + path_fitxers[i] ) ) {
				ResultatAlgorisme aux = comprimirCarpeta_aux(input_path+ File.separator + path_fitxers[i], output_path, algorisme);

				result.setContingut( Concat(result.getContingut(), aux.getContingut()) );
			}
			else {
				if ( ExtensioFitxerCorrecta(path_fitxers[i]) ) {
					ArrayList<Byte> header_fitxer = new ArrayList<>();
					// indiquem que és fitxer
					header_fitxer.add((byte)'f');

					String nom_fitxer = ControladorDades.getNomFitxer(path_fitxers[i]);
					header_fitxer.add((byte)(char)nom_fitxer.length());
					// nom fitxer

					for (int j = 0; j<nom_fitxer.length(); ++j) header_fitxer.add((byte)nom_fitxer.charAt(j));

					Fitxer f = getFitxer(input_path + File.separator + path_fitxers[i]);

					String algorisme_imatge = algorisme;
					if ( f.getExtensio().equals("ppm")) algorisme_imatge = "jpeg";
					ResultatAlgorisme r = comprimirComu(f.getContingut(), algorisme_imatge, f.getExtensio());

					// mida algorisme utilitzat a la compressio
					header_fitxer.add((byte)(char)r.getAlgorisme().length());
					// algorisme utilitzat
					for(int j = 0; j<r.getAlgorisme().length(); ++j) header_fitxer.add((byte)r.getAlgorisme().charAt(j));

					// Allargada del contingut del fitxer
					num_chars_contingut = r.getContingut().length;
					bytes_num_contingut = ByteBuffer.allocate(4).putInt(num_chars_contingut).array();
					for (int j = 0; j< 4; ++j) header_fitxer.add(bytes_num_contingut[j]);

					// contingut fitxer
					for(int j = 0; j<r.getContingut().length; ++j) header_fitxer.add(r.getContingut()[j]);

					// inici fitxer amb un header nou
					byte[] res = new byte[header_fitxer.size()];
					for (int j = 0; j<header_fitxer.size(); ++j)  res[j] = header_fitxer.get(j);

					result.setContingut(Concat(result.getContingut(), res));
				}
			}
		}
		return result;
	}

	//funcio que serveix per cridar la funcio recursiva que descomprimeix una carpeta
	public void descomprimirCarpeta(String input_path, String output_path, String nom_carpeta_arrel) throws IOException {
		byte[] fitxer_comprimit = dades.ControladorDades.getContingutFitxer(input_path);
		int nada = descomprimirCarpeta_i(input_path, output_path, fitxer_comprimit, 0, true, nom_carpeta_arrel );
	}

	//funcio recursiva que descomprimeix una carpeta comprimida i crea tots els arxius que aquesta conté
	private int descomprimirCarpeta_i(String input_path, String output_path, byte[] fitxer_comprimit, int i, boolean primera_carpeta, String nom_carpeta_arrel) throws IOException {

		//caracter c de carpeta
		char carpeta = (char)fitxer_comprimit[i];
		++i;

		//mida nom
		int mida_nom_carpeta = fitxer_comprimit[i];
		++i;

		//nom
		byte[] aux = getSubArrayByte(fitxer_comprimit, i, i+mida_nom_carpeta);
		String nom_carpeta = new String(aux); //Inclou al primer i no al darrer

		if ( primera_carpeta ) nom_carpeta = nom_carpeta_arrel;
		i = i + mida_nom_carpeta;
		dades.ControladorDades.creaCarpeta(output_path, nom_carpeta);

		int num_arxius_carpeta = (int)((fitxer_comprimit[i] & 0xFF ) << 24) | ((fitxer_comprimit[i+1] & 0xFF) << 16) | ((fitxer_comprimit[i+2] & 0xFF) << 8) | ((fitxer_comprimit[i+3] & 0xFF));
		i += 4;

		for ( int j = 0; j < num_arxius_carpeta; ++j ) {
			//carpeta
			if (fitxer_comprimit[i] == 'c' ) {
				byte[] content = getSubArrayByte(fitxer_comprimit, i, fitxer_comprimit.length);
				i += descomprimirCarpeta_i(input_path+File.separator+nom_carpeta, output_path+ File.separator + nom_carpeta, content, 0, false, nom_carpeta_arrel);
			}
			//fitxer
			else if ( fitxer_comprimit[i] == 'f' ) {
				++i;
				int mida_nom_fitxer = fitxer_comprimit[i];
				++i;
				String nom_fitxer = new String(getSubArrayByte(fitxer_comprimit, i, i+mida_nom_fitxer)); //fitxer_comprimit.substring(i, i+mida_nom_fitxer);
				i = i + mida_nom_fitxer;

				int mida_nom_al = fitxer_comprimit[i];
				++i;
				String algorisme = new String(getSubArrayByte(fitxer_comprimit, i, i+mida_nom_al));//fitxer_comprimit.substring(i, i+mida_nom_al );
				i = i + mida_nom_al;
				//Agafem la mida del contingut del fitxer
				int mida_contingut = (int)((fitxer_comprimit[i] & 0xFF ) << 24) | ((fitxer_comprimit[i+1] & 0xFF) << 16) | ((fitxer_comprimit[i+2] & 0xFF) << 8) | ((fitxer_comprimit[i+3] & 0xFF));
				i += 4;
				if (algorisme == "jpeg") --i;
				//String contingut = fitxer_comprimit.substring(i, i+mida_contingut);
				byte[] contingut = getSubArrayByte(fitxer_comprimit, i, i+mida_contingut);
				ResultatAlgorisme r = descomprimirComu(contingut, algorisme);
				String extensio = "txt";
				if (algorisme.equals("jpeg"))  extensio = "ppm";

				dades.ControladorDades.creaFitxer(nom_fitxer, output_path+"/"+nom_carpeta, extensio, r.getContingut());
				i = i + mida_contingut;
			}
		}

		return i;

	}

	// FUNCIONS AUXILIARS PER LA COMPRESSIO DE CARPETES
	/*
	 Funcio que obte el subarray contingut entre els index ini i end de la variable input.
	 PRE: 0 <= ini <= end
	 */
	private byte[] getSubArrayByte(byte[] input, int ini, int end) {
		byte[] result = new byte[end-ini];
		for (int i = 0; i < (end-ini); ++i) result[i] = input[ini+i];
		return result;
	}

	// Concatena dos byte[] en un sol i retorna el resultat.
	private byte[] Concat(byte[] a, byte[] b)
	{
		if (a != null || b != null) {
			int alen, blen;

			if (a == null) alen = 0;
			else alen = a.length;
			if (b == null) blen = 0;
			else blen = b.length;

			byte[] output = new byte[alen + blen];
			for (int i = 0; i < alen; i++)
				output[i] = a[i];
			for (int j = 0; j < blen; j++)
				output[alen + j] = b[j];
			return output;
		}

		byte[] aux = new byte[0];
		return aux;
	}

	// Metode que controla l'extensio dels fitxers dins d'una carpeta
	private boolean ExtensioFitxerCorrecta(String path) throws IOException{
		String extensio = dades.ControladorDades.getExtensioFitxer(path);
		String nom = dades.ControladorDades.getNomFitxer(path);
		switch (extensio) {
			case "txt":
				return true;
			case "ppm":
				return true;
			default:
				throw new IOException("La carpeta conté el fitxer '" + nom +"."+ extensio +"' que no és comprimible.");
		}
	}

	// FUNCIONS PER ACONSEGUIR INFORMACIO EN IMATGES
	// Funcio per trobar l'amplada i altura d'una imatge
	public List<Integer> trobarMidesImatge(byte[] img) {
		Algorisme a = Algorisme.getInstance("jpeg");
		JPEG j = (JPEG) a;
		return j.llegeixHeader(img);
	}

}