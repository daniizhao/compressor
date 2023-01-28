package domini;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class LZ78 extends Algorisme {

	// ATRIBUTS

	// Mida máxima d'entrades que pot tenir el diccionari durant el procés de compressio del contingut d'un fitxer.
	private static final int MAX_LEN = 65500;
	// Única instáncia de l'algorisme LZ78
	private static LZ78 lz78;

	// CONSTRUCTORA
	private LZ78() {
		super("lz78");
	}

	//Funcio getInstance per implementar el patro singleton al projecte.
	public static LZ78 getInstance() {
		if (lz78 == null) {
			lz78 = new LZ78();
		}
		return lz78;
	}

	// METODES

	// Algorisme de compressio de l'algorisme LZ78.
	// PRE: --
	// POST: Es retorna la variable result que conte la compressio de l'input utilitzant el metode de compressio LZ78.
	@Override
	public byte[] comprimirContingut(byte[] input) {
		HashMap<ArrayList<Byte>, Integer> dicc = new HashMap<ArrayList<Byte>, Integer>();
		ArrayList<Byte> w = new ArrayList<>();
		ArrayList<Byte> output = new ArrayList<>();
		int index = 1;

		dicc.put(new ArrayList<Byte>(), 0);

		for (int i = 0; i<input.length; ++i){
			byte c = input[i];
			ArrayList<Byte> key = new ArrayList<>(w);//w + c;
			key.add(c);
			if (dicc.get(key) != null) {
				w = key;
			}
			else {
				if(dicc.size() < MAX_LEN) { //Default

					// Codificacio de l'index del diccionari
					byte[] bytes = ByteBuffer.allocate(4).putInt(dicc.get(w).intValue()+1).array();
					for (int j = 2; j< 4; ++j) output.add(bytes[j]);

					// Afegim el resultat a l'output.
					output.add(c);
					dicc.put(key, index);

					index++;

				} else if(1 < w.size()){ // En el cas que el diccionari ja tingui 65.500 entrades.

					byte c_aux = w.subList(w.size()-1, w.size()).get(0);
					ArrayList<Byte> w_aux = new ArrayList<>(w.subList(0, w.size()-1));

					// Codificacio de l'index del diccionari
					byte[] bytes = ByteBuffer.allocate(4).putInt(dicc.get(w_aux).intValue()+1).array();
					for (int j = 2; j< 4; ++j) output.add(bytes[j]);

					output.add(c_aux);

					i--;
				} else { // En qualssevol altre cas

					//Codificacio de l'index del diccionari
					byte[] bytes = ByteBuffer.allocate(4).putInt(dicc.get(w).intValue()+1).array();
					for (int j = 2; j< 4; ++j) output.add(bytes[j]);

					output.add(c);
				}
				w = new ArrayList<>();
			}
		}

		// Afegim el sobrant a l'output.
		for(int i=0; i<w.size(); i++) {
			//Codificacio de l'index del diccionari
			byte[] bytes = ByteBuffer.allocate(4).putInt(1).array();
			for (int j = 2; j< 4; ++j) output.add(bytes[j]);

			output.add(w.get(i));
		}

		// Obtenim l'array de bytes per retornar el resulat
		byte[] result = new byte[output.size()];
		for (int l = 0; l< output.size(); ++l) result[l] = output.get(l).byteValue();

		return result;
	}

	// Algorisme de Descompressió de l'algorisme LZ78.
	// PRE: La variable input conte una sequencia de bytes resultant de la compressio d'un fitxer simple utilitzant l'algorisme LZ78.
	// POST: Es retorna la variable result que conte la descompressio (contingut original) de l'input utilitzant el metode de descompressio LZ78.
	@Override
	public byte[] descomprimirContingut(byte[] input) {
		HashMap<Integer, ArrayList<Byte>> dicc = new HashMap<Integer, ArrayList<Byte>>();
		ArrayList<Byte> output = new ArrayList<Byte>();
		int index = 1;

		dicc.put(0, new ArrayList<Byte>());

		for(int i = 0; i<input.length; i += 3) {
			//Clau del diccionari
			int key = (int)((input[i] & 0xFF ) << 8) | (input[i+1] & 0xFF);
			--key;

			//Caracter a descodificar
			Byte c = input[i+2];

			//Obtenim la nova entrada del diccionari
			ArrayList<Byte> aux = new ArrayList<>(dicc.get(key));
			aux.add(c);

			//Actualitzem el diccionari
			dicc.put(index, aux);
			output.addAll(aux);
			index++;
		}

		// Obtenim l'array de Bytes per retornar el resultat.
		byte[] result = new byte[output.size()];
		for (int l = 0; l< output.size(); ++l) result[l] = output.get(l).byteValue();

		return result;
	}

}
