package domini;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class LZSS extends Algorisme {

	// CONSTANTS
	private static final int MIN_LEN = 3;
	private static final int WINDOWSIZE = 4096;	//12 bits
	private static final int MAX_MATCH = 15;	//4 bits
	
	private static LZSS lzss;
	
	// CONSTRUCTORES
	private LZSS() {
		super("lzss");
	}
	
	public static LZSS getInstance() {
		if(lzss == null) {
			lzss = new LZSS();
			
		}
		return lzss;
	}
	
	// METODES

	/*
	PRE: input correcte.
	POST: Retorna un byte array amb el input comprimit.
	Compressio del byte array amb l'algorisme LZSS.
	 */
	@Override
	public byte[] comprimirContingut(byte[] input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		HashMap<Byte, HashSet<Integer>> diccionari = new HashMap<>();

		byte carac;
		int pos_input = 0;

		while(pos_input < input.length) {
			byte mask = 0x0000;
			boolean codifica;
			int offset;
			int longitud;

			ByteArrayOutputStream part_cod = new ByteArrayOutputStream();

			// per codificar la mascara de 8 bits i 8 codificacions
			for(int i = 0; i<8 && pos_input<input.length; i++) {
				// inicialitzem parametres
				codifica = false;
				offset = WINDOWSIZE;
				longitud = 0;

				carac = input[pos_input];

				// si es troba al diccionari
				if(diccionari.containsKey(carac)) {
					longitud = 1;

					// obtenim el conjunt amb les posicions trobades anteriorment
					HashSet<Integer> pos_trobades = diccionari.get(carac);

					for(Iterator<Integer> it = pos_trobades.iterator(); it.hasNext();) {
						int pt = it.next();
						int aux_offset = pos_input-pt;
						// si es troba a una distancia superior al tamany del buffer, l'eliminem del conjunt (ja no ens interessa)
						if(aux_offset > WINDOWSIZE) {
							it.remove();
							continue;
						}

						int match_len = obtenirMatchLength(input, pos_input+1, pt+1);

						// si la longitud trobada es mes gran que l'actual i l'offset es mes petit, ens guardem les dades
						if(match_len>MIN_LEN && match_len>=longitud && aux_offset<offset) {
							longitud = match_len;
							offset = aux_offset;
							codifica = true;
						}
					}
				}
				else { 			// si no es troba al diccionari, l'afegim
					HashSet<Integer> ini_pos = new HashSet<>();
					ini_pos.add(pos_input);
					diccionari.put(carac, ini_pos);
				}

				// afegim les posicions dels caracters que ja hem codificat
				int maxj = Math.min(pos_input+longitud, input.length);
				for(int j = pos_input; j<maxj; j++) {
					HashSet<Integer> ps = diccionari.get(input[j]);
					if(ps == null) {
						ps = new HashSet<>();
						diccionari.put(input[j], ps);
					}
					ps.add(j);
				}

				// si codifica, guardem la longitud i l'offset en dos bytes
				if(codifica) {
					mask |= (1 << i);
					byte b1 = (byte) ((offset<<4) | (longitud&0x000F));
					byte b2 = (byte) (offset>>>4);

					part_cod.write(b1);
					part_cod.write(b2);
					pos_input += longitud;
				}
				else {	// si no, afegim el literal directament
					part_cod.write(carac);
					pos_input++;
				}
			}
			// afegim el byte de mascara i les 8 codificacions
			output.write(mask);
			output.write(part_cod.toByteArray());
		}
		return output.toByteArray();
	}


	// Funcio auxiliar per buscar la longitud de coincidencia.
	private int obtenirMatchLength(byte[] src, int pos_ba, int pos_bb) {
		int ml = 1;
		while(ml < MAX_MATCH && pos_ba < src.length && src[pos_ba] == src[pos_bb]) {
			ml++;
			pos_ba++;
			pos_bb++;
		}
		return ml;
	}

	/*
	PRE: input es el contingut comprimit previament amb aquest algorisme.
	POST: Retorna un byte array amb el input descomprimit.
	Descompressio del byte array amb l'algorisme LZSS.
	 */
	@Override
	public byte[] descomprimirContingut(byte[] input) {
		List<Byte> output = new ArrayList<>();
		int pos_input = 0;
		int b1, b2;
		int mascara;

		while(pos_input < input.length) {
			mascara = input[pos_input];
			pos_input++;
			for(int i = 0; i<8 && pos_input<input.length; i++) {
				if( (mascara&0x0001) == 1) {
					b1 = input[pos_input] & 0xFF;
					pos_input++;
					b2 = input[pos_input] & 0xFF;

					int matchlength = b1 & 0x0F;
					int offset  = ((b2 & 0xFF) << 8) | (b1 & 0xF0);
					offset = offset >>> 4;

					int pos_buscar = output.size() - offset;
					for(int j = 0; j<matchlength; j++) {
						output.add(output.get(pos_buscar));
						pos_buscar++;
					}
				}
				else {
					output.add(input[pos_input]);
				}
				pos_input++;
				mascara = (byte) (mascara>>>1);
			}
		}

		// passar de ArrayList a byte Array
		byte[] res = new byte[output.size()];
		for(int i =0; i<output.size(); i++) res[i] = output.get(i);

		return res;
	}
	
}
