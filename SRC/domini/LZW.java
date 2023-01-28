package domini;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class LZW extends Algorisme{

	//CONSTANTS
	private static LZW lzw;

	//CONSTRUCTORES
	public LZW() {
		super("lzw");
	}

	//obtenir instància de l'algorisme lzw
	public static LZW getInstance() {
		if ( lzw == null ) {
			lzw = new LZW();
		}
		return lzw;
	}

	//METODES

	/*
	Pre: --
	Post: Retorna un byte array amb el input comprimit.
	Compressio del byte array amb l'algorisme LZW.
	 */
	public byte[] comprimirContingut(byte[] contingut) throws IOException {

		HashMap<ArrayList<Byte>,Integer>diccionari = new HashMap<ArrayList<Byte>,Integer>();
		ArrayList<Byte> output = new ArrayList<>();

		if ( contingut.length == 0 ) {
			byte[] result = new byte[0];
			return result;
		}

		//iniciem el map amb tots els valors ascii
		int k;
		for ( k = 0; k < 256; k++ ) {
			ArrayList<Byte> c = new ArrayList<>();
			c.add((byte)k);
			diccionari.put(c, k);
		}

		//codi de compressió
		ArrayList<Byte> w = new ArrayList<>();
		int i = 0;
		k = 255;
		while ( i < contingut.length ) {
			ArrayList<Byte> var = new ArrayList<>();
			var.add(contingut[i]);
			ArrayList<Byte> aux = new ArrayList<>();
			aux.addAll(w);
			aux.addAll(var);

			if ( diccionari.containsKey(aux) ) w = aux;
			else {
				byte[] valor = ByteBuffer.allocate(4).putInt(diccionari.get(w)).array();
				output.add(valor[2]);
				output.add(valor[3]);
				++k;
				if ( k < 0xFFFF ) diccionari.put(aux, k);
				w = var;
			}
			++i;
		}
		byte[] valor = ByteBuffer.allocate(4).putInt(diccionari.get(w)).array();
		output.add(valor[2]);
		output.add(valor[3]);
		byte[] result = new byte[output.size()];
		for (int l = 0; l < output.size(); ++l ) result[l] = output.get(l).byteValue();
		return result;
	}

	/*
	Pre: --
	Post: Retorna un byte array amb el input descomprimit.
	Descompressio del byte array amb l'algorisme LZW.
	 */
	public byte[] descomprimirContingut(byte[] contingut) throws IOException {

		//si el fitxer es buit es retorna un array de bytes buit
		if ( contingut.length == 0 ) {
			byte[] result = new byte[0];
			return result;
		}

		Integer codi_vell;
		Integer codi_nou;
		ArrayList<Byte> cadena = new ArrayList<>();
		HashMap<Integer,ArrayList<Byte>>diccionari = new HashMap<Integer,ArrayList<Byte>>();

		//inicialitzem map amb els caràcters ascii
		int k;
		for ( k = 0; k < 256; ++k ) {
			ArrayList<Byte> c = new ArrayList<Byte>();
			c.add((byte)k);
			diccionari.put(k, c);
		}
		k = 256;

		codi_vell = ((contingut[0] & 0xFF )<<8 | (contingut[1] & 0xFF));
		ArrayList caracter = new ArrayList<>(diccionari.get(codi_vell));
		ArrayList<Byte> output = new ArrayList<Byte>(caracter);

		int i = 2;
		int j = 2;
		while ( i < (contingut.length - 1) ) {
			codi_nou = ((contingut[j] & 0xFF )<<8 | (contingut[j+1] & 0xFF));
			j+=2;
			if (!diccionari.containsKey(codi_nou)) {
				cadena = new ArrayList<>(diccionari.get(codi_vell));
				cadena.addAll(caracter);
			}
			else {
				cadena = new ArrayList<>(diccionari.get(codi_nou));
			}
			output.addAll(cadena);
			caracter = new ArrayList<>();
			caracter.add(cadena.get(0));

			ArrayList<Byte> aux_codivell = new ArrayList<>(diccionari.get(codi_vell));
			aux_codivell.addAll(caracter);
			diccionari.put(k,aux_codivell);
			++k;
			codi_vell = codi_nou;
			i+=2;
		}
		byte[] result = new byte[output.size()];
		for ( int l = 0; l < output.size(); ++l ) {
			result[l] = output.get(l).byteValue();
		}
		return result;
	}
}