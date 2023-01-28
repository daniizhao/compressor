package domini;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JPEG extends Algorisme {
	private static JPEG jpeg;

	private static final int[][] MATQ50 = new int[][] {
			{16,11,10,16,24,40,51,61},
			{12,12,14,19,26,58,60,55},
			{14,13,16,24,40,57,69,56},
			{14,17,22,29,51,87,80,62},
			{18,22,37,56,68,109,103,77},
			{24,35,55,64,81,104,113,92},
			{49,64,78,87,103,121,120,101},
			{72,92,95,98,112,100,103,99}
	};

	private static final int[][] MATQ = new int[][] {
			{9,6,5,9,13,22,29,35},
			{6,6,8,11,15,33,34,30},
			{8,7,9,13,22,33,39,31},
			{8,9,12,16,28,49,45,34},
			{10,12,21,32,39,61,58,42},
			{13,19,31,36,45,58,63,51},
			{28,36,44,49,58,68,66,55},
			{41,52,54,55,62,56,57,54}
	};

	private boolean mostreig; // cert si volem mostreig 4:2:0
	private boolean quant50; // cert si volem fer servir matq50

	private JPEG() {
	    super("jpeg");
		this.mostreig = false;
		this.quant50 = true;
	}

	public static JPEG getInstance() {
		if (jpeg == null) {
			jpeg = new JPEG();
		}
		return jpeg;
	}

	public void setMostreig(boolean b) {
		this.mostreig = b;
	}

	public void setQuant(boolean b) {
		this.quant50 = b;
	}

	@Override
	public byte[] comprimirContingut(byte[] input) throws IOException {
	    /*
	    Pre: 'input' es el contingut d'un fitxer amb format ppm
	    Post: S'ha comprimit el contingut del fitxer ppm amb l'algorisme JPEG
	     */
		//Llegim l'array de bytes corresponent al fitxer d'entrada i guardem els valors dels pixels en 3 matrius,
		//una per cada component (R, G i B). Primer llegim la capçalera per samber les mides de la imatge.
		List<Integer> h = llegeixHeader(input);
		int cols = h.get(0);
		int files = h.get(1);
		int nmax = h.get(2);
		int index = h.get(3);

		//si la imatge es buida simplement es retorna una capçalera per indicar que el fitxer comprimit te una imatge buida
		if (cols == 0 && files == 0) return new byte[] {'0',' ','0',' '};

		//Un cop llegida la capçalera podem omplir les matrius amb els valors corresponents
		//Si el rang no es [0..255] ho corregim
		boolean conversio = false;
		if (nmax != 255) conversio = true;

		int[][] r = new int[files][cols];
		int[][] g = new int[files][cols];
		int[][] b = new int[files][cols];

		for (int i=0; i<files; ++i) {
			for (int j=0; j<cols; ++j) {
				int inpR = (input[index]&0x0FF); //Lector de P6
				++index;
				int inpG = (input[index]&0x0FF);
				++index;
				int inpB = (input[index]&0x0FF);
				++index;

				if (conversio) {
					inpR = inpR/nmax * 255;
					inpG = inpG/nmax * 255;
					inpB = inpB/nmax * 255;
				}
				r[i][j] = inpR;
				g[i][j] = inpG;
				b[i][j] = inpB;
			}
		}

		//Transformacio a YCbCr i mostreig 4:2:0 de Cb i Cr si es necessari
		int[][] y = new int[files][cols];
		int[][] cb;
		int[][] cr;

		for (int i=0; i<files; i++) {
			for (int j=0; j<cols; j++)
				y[i][j] = (int)Math.round(0.257 * r[i][j] + 0.504 * g[i][j] + 0.098 * b[i][j] + 16);
		}

		if (mostreig) {
			int filesM = (int)Math.ceil(files/2);
			int colsM = (int)Math.ceil(cols/2);
			cb = new int[filesM][colsM];
			cr = new int[filesM][colsM];

			int i_m = 0;
			int j_m = 0;
			for (int i=0; i<files; i+=2) {
				for (int j=0; j<cols; j+=2) {
					cb[i_m][j_m] = (int)Math.round(-0.148 * r[i][j] - 0.291 * g[i][j] + 0.439 * b[i][j] + 128);
					cr[i_m][j_m] = (int)Math.round(0.439 * r[i][j] - 0.368 * g[i][j] - 0.071 *b[i][j] + 128);
					j_m++;
				}
				i_m++;
				j_m = 0;
			}
		} else {
			cb = new int[files][cols];
			cr = new int[files][cols];

			for (int i=0; i<files; i++) {
				for (int j=0; j<cols; j++) {
					cb[i][j] = (int)Math.round(-0.148 * r[i][j] - 0.291 * g[i][j] + 0.439 * b[i][j] + 128);
					cr[i][j] = (int)Math.round(0.439 * r[i][j] - 0.368 * g[i][j] - 0.071 *b[i][j] + 128);
				}
			}
		}

		//Per tal de poder fer una divisio en blocs de 8x8, ampliem la matriu si als extrems hi ha blocs que no quedarien complets
		//Com que Cb i Cr poden tenir una mida diferent pel mostreig, es tracten a part d'Y
		if (files % 8 != 0 || cols % 8 != 0) {
			int lenV = files;
			int lenH = cols;
			while (lenH % 8 != 0) ++lenH;
			while (lenV % 8 != 0) ++lenV;
			int[][] aux = new int[lenV][lenH];
			for (int i=0; i<lenV; ++i) {
				if (i<y.length) {
					for (int j=0; j<lenH; ++j) {
						if (j<y[0].length) aux[i][j] = y[i][j];
						else aux[i][j] = aux[i][j-1];
					}
				} else {
					for (int j=0; j<lenH; ++j)
						aux[i][j] = aux[i-1][j];
				}
			}
			y=aux;
		}
		if (cb.length % 8 != 0 || cb[0].length % 8 != 0) {
			int lenV = cb.length;
			int lenH = cb[0].length;
			while (lenH % 8 != 0) ++lenH;
			while (lenV % 8 != 0) ++lenV;
			int[][] aux2 = new int[lenV][lenH];
			int[][] aux3 = new int[lenV][lenH];
			for (int i=0; i<lenV; ++i) {
				if (i<cb.length) {
					for (int j=0; j<lenH; ++j) {
						if (j<cb[0].length) {
							aux2[i][j] = cb[i][j];
							aux3[i][j] = cr[i][j];
						} else {
							aux2[i][j] = aux2[i][j-1];
							aux3[i][j] = aux3[i][j-1];
						}
					}
				} else {
					for (int j=0; j<lenH; ++j) {
						aux2[i][j] = aux2[i-1][j];
						aux3[i][j] = aux3[i-1][j];
					}
				}
			}
			cb=aux2;
			cr=aux3;
		}
		//Transformacio discreta del cosinus (DCT)
		//Novament hem de separar Y per una part i Cb/Cr per l'altra degut al mostreig
		double[][] y_dct = new double[y.length][y[0].length];
		double[][] cb_dct = new double[cb.length][cb[0].length];
		double[][] cr_dct = new double[cr.length][cr[0].length];

		for (int i=0; i<y.length; i+=8) {
			for (int j=0; j<y[0].length; j+=8) {
				//per cada bloc 8x8 - component Y
				dctBloc8(y, y_dct, i, j);
			}
		}
		for (int i=0; i<cb.length; i+=8) {
			for (int j=0; j<cb[0].length; j+=8) {
				//per cada bloc 8x8 - component Cb
				dctBloc8(cb, cb_dct, i, j);
				//per cada bloc 8x8 - component Cr
				dctBloc8(cr, cr_dct, i, j);
			}
		}
		//Després del DCT obtindrem valors amb decimals, que eliminarem a l'arrodonir a la quantitzacio
		//Quantitzacio. Segons la configuracio de quantitzacio, triarem una de les dues matrius de quantitzacio
		if (quant50) {
			quantitzaMatriu(y_dct, y, MATQ50);
			quantitza2Matrius(cb_dct, cr_dct, cb, cr, MATQ50);
		} else {
			quantitzaMatriu(y_dct, y, MATQ);
			quantitza2Matrius(cb_dct, cr_dct, cb, cr, MATQ);
		}

		// Creem la capçalera del fitxer comprimit
		ByteArrayOutputStream baOutput = new ByteArrayOutputStream();
		//Contingut capçalera: amplada original, alçada original, amplada despres divisio blocs, alçada despres divisio blocs
		char[] header = (cols + " " + files + " " + y[0].length + " " + y.length + " ").toCharArray();
		for (char c : header) baOutput.write((byte)c);
		//Afegim un byte amb flags per saber la configuracio de mostreig i quantitzacio utilitzada
		int flags = 0;
		if (mostreig) flags = flags | 0x02;
		if (quant50) flags = flags | 0x01;
		baOutput.write((byte)flags);

		//Passem de 3 matrius a un unic array de bytes utilitzant zig-zag. Tambe eliminem series de molts zeros consecutius.
		ArrayList<Byte> zigzaguejat = new ArrayList<>();
		for (int i=0; i<y.length; i+=8) {
			for (int j=0; j<y[0].length; j+=8) {
				//un bloc 8x8
				codificaZigZagCompacta(y,zigzaguejat,i,j);
			}
		}

		for (int i=0; i<cb.length; i+=8) {
			for (int j=0; j<cb[0].length; j+=8) {
				//un bloc 8x8
				codificaZigZagCompacta(cb,zigzaguejat,i,j);
			}
		}

		for (int i=0; i<cr.length; i+=8) {
			for (int j=0; j<cr[0].length; j+=8) {
				//un bloc 8x8
				codificaZigZagCompacta(cr,zigzaguejat,i,j);
			}
		}

		//Generem un arbre per Huffman i després fem la compressio utilitzant aquest arbre
		HuffmanJPEG huff = new HuffmanJPEG();
		baOutput.write(huff.construirArbre(zigzaguejat));
		baOutput.write(huff.comprimeix(zigzaguejat));
		return baOutput.toByteArray();
	}

	public List<Integer> llegeixHeader(byte[] input) {
		/*
		Pre: 'input' es el contingut d'un fitxer ppm
		Post: S'ha retornat una llista amb l'amplada, l'alçada, el rang maxim dels pixels i la primera posicio
		de 'input' despres de la capçalera, en aquest ordre
		 */
		List<Integer> header = new ArrayList<>();
		if (input[0] != 'P' || input[1] != '6') throw new NumberFormatException("El fitxer ppm no es del tipus P6");

		int index = 3;
		String auxLecturaHeader = "";

		if (input[index]=='#') {
			index = saltaComentari(input, index);
		}
		//Amplada
		while (input[index] != ' ' && input[index] != '\n') {
			auxLecturaHeader+=(char)input[index];
			index++;
		}
		header.add(Integer.parseInt(auxLecturaHeader));
		auxLecturaHeader = "";
		++index;

		if (input[index]=='#') {
			index = saltaComentari(input, index);
		}
		//Alçada
		while (input[index] != ' ' && input[index] != '\n') {
			auxLecturaHeader+=(char)input[index];
			index++;
		}
		header.add(Integer.parseInt(auxLecturaHeader));
		auxLecturaHeader = "";
		++index;

		if (input[index]=='#') {
			index = saltaComentari(input, index);
		}
		//Rang maxim
		while (input[index] != ' ' && input[index] != '\n') {
			auxLecturaHeader+=(char)input[index];
			index++;
		}
		header.add(Integer.parseInt(auxLecturaHeader));
		++index;
		header.add(index);
		return header;
	}

	private int saltaComentari(byte[] input, int index) {
	    //Metode per saltar un comentari a la capçalera ppm
		do {
			index++;
		} while (input[index] != '\n');
		index++;
		return index;
	}

	private void dctBloc8(int[][] mat_in, double[][] mat_out, int i, int j) {
	    //Metode per aplicar la transformacio discreta del cosinus a un bloc 8x8 indicat per 'i' i 'j'
		for (int i2=i; i2<i+8; ++i2) {
			for (int j2=j; j2<j+8; ++j2) {
				mat_in[i2][j2] -= 128;
			}
		}
		for (int i2=i; i2<i+8; ++i2) {
			for (int j2=j; j2<j+8; ++j2) {
				double alfa_u, alfa_v;
				if (j2-j == 0) alfa_u = 0.707106781; // 1/sqrt(2)
				else alfa_u = 1;
				if (i2-i == 0) alfa_v = 0.707106781; // 1/sqrt(2)
				else alfa_v = 1;
				//sumatori
				double resultat = 0;
				for (int sx=0; sx<8; ++sx) {
					for (int sy=0; sy<8; ++sy) {
						double cos1 = Math.cos(((2*sx+1)*(j2-j)*Math.PI)/16);
						double cos2 = Math.cos(((2*sy+1)*(i2-i)*Math.PI)/16);
						resultat += mat_in[i+sy][j+sx] * cos1 * cos2;
					}
				}
				mat_out[i2][j2] = 0.25 * alfa_u * alfa_v * resultat;
			}
		}
	}

	private void quantitzaMatriu(double[][] mat_in, int[][] mat_out, int[][] matriuQuant) {
	    //Metode per aplicar la quantitzacio a una matriu
		for (int i=0; i<mat_out.length; i+=8) {
			for (int j=0; j<mat_out[0].length; j+=8) {
				for (int n=i; n<i+8; ++n) {
					for (int m=j; m<j+8; ++m)
						mat_out[n][m] = (int)Math.round(mat_in[n][m]/matriuQuant[n-i][m-j]);
				}
			}
		}
	}

	private void quantitza2Matrius(double[][] mat1_in, double[][] mat2_in, int[][] mat1_out, int[][] mat2_out, int[][] matriuQuant) {
		//Metode per aplicar la auantitzacio a dues matrius a la vegada
	    for (int i=0; i<mat1_out.length; i+=8) {
			for (int j=0; j<mat1_out[0].length; j+=8) {
				for (int n=i; n<i+8; ++n) {
					for (int m=j; m<j+8; ++m) {
						mat1_out[n][m] = (int)Math.round(mat1_in[n][m]/matriuQuant[n-i][m-j]);
						mat2_out[n][m] = (int)Math.round(mat2_in[n][m]/matriuQuant[n-i][m-j]);
					}
				}
			}
		}
	}

	//Atributs necessaris pel zig-zag
	private boolean amuntZigZag;
	private int altZigZag;
	private int ampZigZag;
	private int filaZigZag;
	private int colZigZag;

	private void codificaZigZagCompacta(int[][] mat, ArrayList<Byte> result, int fInici, int cInici) {
	    //Metode per aplicar el zig-zag a un bloc 8x8 indicat per 'fInici' i 'cInici'
		amuntZigZag = true;
		altZigZag = fInici+8;
		ampZigZag = cInici+8;
		filaZigZag = fInici;
		colZigZag = cInici;
		int comptZeros = 0;
		while (filaZigZag < altZigZag && colZigZag < ampZigZag) {

			if (mat[filaZigZag][colZigZag] == 0) comptZeros++;
			else {
				if (comptZeros > 1) {
					result.add((byte) 127);
					result.add((byte) comptZeros);
					comptZeros = 0;
				} else if (comptZeros == 1) {
					result.add((byte) 0);
					comptZeros = 0;
				}
				result.add((byte)mat[filaZigZag][colZigZag]);
			}

			calculaSeguentPosZigZag();
		}
		if (comptZeros > 1) {
			result.add((byte) 127);
			result.add((byte) comptZeros);
		} else if (comptZeros == 1) {
			result.add((byte) 0);
		}
	}

	private void calculaSeguentPosZigZag() {
	    //Calcula la seguent posicio a tractar per al zig-zag
		if (amuntZigZag) {
			if (colZigZag+1 >= ampZigZag) {
				filaZigZag++;
				amuntZigZag = false;
			} else if (filaZigZag-1 < altZigZag-8) {
				colZigZag++;
				amuntZigZag = false;
			} else {
				filaZigZag--;
				colZigZag++;
			}
		} else {
			if (filaZigZag+1 >= altZigZag) {
				colZigZag++;
				amuntZigZag = true;
			} else if (colZigZag-1 < ampZigZag-8) {
				filaZigZag++;
				amuntZigZag = true;
			} else {
				filaZigZag++;
				colZigZag--;
			}
		}
	}

	@Override
	public byte[] descomprimirContingut(byte[] input) {
	    /*
	    Pre: 'input' es el contingut d'un fitxer comprimit amb aquest algorisme
	    Post: S'ha descomprimit la imatge i es retorna un array de bytes amb el ppm resultant
	     */
		//Llegim el contingut del fitxer comprimit
		//Comencem per la capçalera per saber les mides de la imatge
		int ampleO, altO, ampleB, altB;

		int index = 0;
		String auxLecturaHeader = "";

		while (input[index] != ' ') {
			auxLecturaHeader+=(char)input[index];
			++index;
		}
		ampleO = Integer.parseInt(auxLecturaHeader);
		auxLecturaHeader = "";
		++index;

		while (input[index] != ' ') {
			auxLecturaHeader+=(char)input[index];
			++index;
		}
		altO = Integer.parseInt(auxLecturaHeader);
		auxLecturaHeader = "";
		++index;
		//si la imatge es buida retornem una capçalera d'imatge ppm buida
		if (ampleO == 0 && altO == 0) return new byte[] {'P','6',' ','0',' ','0',' ','2','5','5','\n'};

		while (input[index] != ' ') {
			auxLecturaHeader+=(char)input[index];
			++index;
		}
		ampleB = Integer.parseInt(auxLecturaHeader);
		auxLecturaHeader = "";
		++index;

		while (input[index] != ' ') {
			auxLecturaHeader+=(char)input[index];
			++index;
		}
		altB = Integer.parseInt(auxLecturaHeader);
		++index;

		int[][] y = new int[altB][ampleB];
		int[][] cb, cr;
		int altCbCr = altB;
		int ampleCbCr = ampleB;

		//Llegim el byte de flags per obtenir la configuracio de mostreig i quantitzacio que es va fer servir al comprimir
		byte c = input[index];
		if ((c & 0x02) == 0) {
			mostreig = false;
			cb = new int[altB][ampleB];
			cr = new int[altB][ampleB];
		}
		else {
			mostreig = true;
			altCbCr = altCbCr/2; //Multiple de 8 dividit entre 2 es multiple de 4 i de vegades ho segueix sent de 8
			if (altCbCr%8 != 0) altCbCr += 4; //Si no es multiple de 8 li sumem 4 perque ho sigui
			ampleCbCr = ampleCbCr/2;
			if (ampleCbCr%8 != 0) ampleCbCr += 4;
			cb = new int[altCbCr][ampleCbCr];
			cr = new int[altCbCr][ampleCbCr];
		}
		if ((c & 0x01) == 0) quant50 = false;
		else quant50 = true;
		++index;

		//Llegim l'arbre per poder obtenir els codis corresopnents a cada valor de la imatge comprimida
		//Despres desfem Huffman mitjançant l'arbre llegit
		HuffmanJPEG huff = new HuffmanJPEG();
		huff.llegirArbre(input,index);
		ArrayList<Byte> zigzaguejat = huff.descomprimeix(input);

		//Desfem el zig-zag i recuperem els zeros que hem "compactat" per poder tornar construir les matrius Y, Cb i Cr
		for (int i=0; i<altB; i+=8) {
			for (int j=0; j<ampleB; j+=8) {
				//un bloc 8x8
				descodificaZigZagDescompacta(zigzaguejat,y,i,j);
			}
		}

		for (int i=0; i<altCbCr; i+=8) {
			for (int j=0; j<ampleCbCr; j+=8) {
				//un bloc 8x8
				descodificaZigZagDescompacta(zigzaguejat,cb,i,j);
			}
		}

		for (int i=0; i<altCbCr; i+=8) {
			for (int j=0; j<ampleCbCr; j+=8) {
				//un bloc 8x8
				descodificaZigZagDescompacta(zigzaguejat,cr,i,j);
			}
		}

		//Desfem la quantitzacio utilitzant la mateixa matriu que es va fer servir a la compressio
		if (quant50) {
			desquantitzaMatriu(y, MATQ50);
			desquantitza2Matrius(cb, cr, MATQ50);
		} else {
			desquantitzaMatriu(y, MATQ);
			desquantitza2Matrius(cb, cr, MATQ);
		}

		//Desfem transformacio discreta del cosinus
		//Hem de separar Y i Cb/Cr pel mostreig, igual que a la compressio
		int[][] y_d = new int[altB][ampleB];
		int[][] cb_d = new int[cb.length][cb[0].length];
		int[][] cr_d = new int[cr.length][cr[0].length];

		for (int i=0; i<y.length; i+=8) {
			for (int j=0; j<y[0].length; j+=8) {
				//per cada bloc 8x8 - component Y
				desferDctBloc8(y, y_d, i, j);
				for (int i2=i; i2<i+8; ++i2) {
					for (int j2=j; j2<j+8; ++j2) {
						y_d[i2][j2] += 128;
					}
				}
			}
		}
		for (int i=0; i<cb.length; i+=8) {
			for (int j=0; j<cb[0].length; j+=8) {
				//per cada bloc 8x8 - component Cb
				desferDctBloc8(cb, cb_d, i, j);
				for (int i2=i; i2<i+8; ++i2) {
					for (int j2=j; j2<j+8; ++j2) {
						cb_d[i2][j2] += 128;
					}
				}
				//per cada bloc 8x8 - component Cr
				desferDctBloc8(cr, cr_d, i, j);
				for (int i2=i; i2<i+8; ++i2) {
					for (int j2=j; j2<j+8; ++j2) {
						cr_d[i2][j2] += 128;
					}
				}
			}
		}

		//Recuperem les mides originals de la imatge eliminant els pixels redundants per poder fer la divisio en blocs 8x8
		//Tambe desfem el mostreig si al comprimir es va aplicar aquesta opcio
		if (altO != altB || ampleO != ampleB) {
			int[][] aux1 = new int[altO][ampleO];
			for (int i=0; i<altO; ++i) {
				for (int j=0; j<ampleO; ++j)
					aux1[i][j] =y_d[i][j];
			}
			if (!mostreig) {
				int[][] aux2 = new int[altO][ampleO];
				int[][] aux3 = new int[altO][ampleO];
				for (int i=0; i<altO; ++i) {
					for (int j=0; j<ampleO; ++j) {
						aux2[i][j] =cb_d[i][j];
						aux3[i][j] =cr_d[i][j];
					}
				}
				cb_d = aux2;
				cr_d = aux3;
			}
			y_d = aux1;
		}

		if (mostreig) {
			int[][] aux2 = new int[altO][ampleO];
			int[][] aux3 = new int[altO][ampleO];

			int i_m = 0;
			int j_m = 0;

			for (int i=0; i<altO; i+=2) {
				for (int j=0; j<ampleO; j+=2) {
					aux2[i][j] = cb_d[i_m][j_m];
					aux3[i][j] = cr_d[i_m][j_m];
					if (j+1 < ampleO) {
						aux2[i][j+1] = cb_d[i_m][j_m];
						aux3[i][j+1] = cr_d[i_m][j_m];
					}
					if (i+1 < altO) {
						aux2[i+1][j] = cb_d[i_m][j_m];
						aux3[i+1][j] = cr_d[i_m][j_m];
					}
					if (i+1 < altO && j+1 < ampleO) {
						aux2[i+1][j+1] = cb_d[i_m][j_m];
						aux3[i+1][j+1] = cr_d[i_m][j_m];
					}
					j_m++;
				}
				i_m++;
				j_m = 0;
			}
			cb_d = aux2;
			cr_d = aux3;
		}

		//Passem de l'espai de color YCbCr a RGB, ja que les imatges ppm s'han de desar d'aquesta manera
		int[][] r = new int[altO][ampleO];
		int[][] g = new int[altO][ampleO];
		int[][] b = new int[altO][ampleO];

		for (int i=0; i<altO; i++) {
			for (int j=0; j<ampleO; j++) {
				r[i][j] = (int)Math.round(1.164*(y_d[i][j] - 16) + 1.596*(cr_d[i][j] - 128));
				if (r[i][j]<0) r[i][j]=0;
				if (r[i][j]>255) r[i][j]=255;
				g[i][j] = (int)Math.round(1.164*(y_d[i][j] - 16) - 0.813*(cr_d[i][j] - 128) - 0.391*(cb_d[i][j] - 128));
				if (g[i][j]<0) g[i][j]=0;
				if (g[i][j]>255) g[i][j]=255;
				b[i][j] = (int)Math.round(1.164*(y_d[i][j] - 16) + 2.018*(cb_d[i][j] - 128));
				if (b[i][j]<0) b[i][j]=0;
				if (b[i][j]>255) b[i][j]=255;
			}
		}

		//Elaborem la imatge ppm
		//Primer afegim la capçalera amb "P6", l'amplada, l'alçada i el rang dels valors dels pixels [0..255]
		//Despres afegim el valor de R, G i B per cada pixel de la imatge descomprimida
		ByteArrayOutputStream baOutput= new ByteArrayOutputStream();
		char[] header = ("P6 "+ampleO+" "+altO+" 255\n").toCharArray();
		for (char ch : header) baOutput.write((byte)ch);
		for (int i=0; i<altO; ++i) {
			for (int j=0; j<ampleO; ++j) {
				baOutput.write((byte)r[i][j]);
				baOutput.write((byte)g[i][j]);
				baOutput.write((byte)b[i][j]);
			}
		}
		return baOutput.toByteArray();
	}

	private void desferDctBloc8(int[][] mat_in, int[][] mat_out, int i, int j) {
	    //Desfa la transformacio discreta del cosinus al bloc 8x8 indicat per 'i' i 'j'
		for (int i2=i; i2<i+8; ++i2) {
			for (int j2=j; j2<j+8; ++j2) {
				//sumatori
				double resultat = 0;
				for (int su=0; su<8; ++su) {
					for (int sv=0; sv<8; ++sv) {
						double alfa_u, alfa_v;
						if (su == 0) alfa_u = 0.707106781; // 1/sqrt(2)
						else alfa_u = 1;
						if (sv == 0) alfa_v = 0.707106781; // 1/sqrt(2)
						else alfa_v = 1;
						double cos1 = Math.cos(((2*(j2-j)+1)* su * Math.PI) / 16);
						double cos2 = Math.cos(((2*(i2-i)+1)* sv * Math.PI) / 16);
						resultat += alfa_u * alfa_v * mat_in[i+sv][j+su] * cos1 * cos2;
					}
				}
				mat_out[i2][j2] = (int)Math.round(0.25*resultat);
			}
		}
	}

	private void desquantitzaMatriu(int[][] mat, int[][] matriuQuant) {
	    //Desfa la quantitzacio d'una matriu
		for (int i = 0; i < mat.length; i += 8) {
			for (int j = 0; j < mat[0].length; j += 8) {
				for (int n = i; n < i + 8; ++n) {
					for (int m = j; m < j + 8; ++m)
						mat[n][m] = mat[n][m] * matriuQuant[n - i][m - j];
				}
			}
		}
	}

	private void desquantitza2Matrius(int[][] mat1, int[][] mat2, int[][] matriuQuant) {
	    //Desfa la quantitzacio de dues matrius a la vegada
		for (int i=0; i<mat1.length; i+=8) {
			for (int j=0; j<mat1[0].length; j+=8) {
				for (int n=i; n<i+8; ++n) {
					for (int m=j; m<j+8; ++m) {
						mat1[n][m] = mat1[n][m]*matriuQuant[n-i][m-j];
						mat2[n][m] = mat2[n][m]*matriuQuant[n-i][m-j];
					}
				}
			}
		}
	}

	private void descodificaZigZagDescompacta(ArrayList<Byte> input, int[][] mat, int fInici, int cInici) {
	    //Desfa el zig-zag aplicat a 'input' i desa els valors a les posicions corresponents de les matrius
		amuntZigZag = true;
		int zerosPendents = 0;
		this.altZigZag = fInici+8;
		this.ampZigZag = cInici+8;
		filaZigZag = fInici;
		colZigZag = cInici;
		int posicionsEscrites = 0;
		while (posicionsEscrites != 64) {

			if (zerosPendents == 0) {
				byte actual = input.remove(0);
				if (actual == 127) {
					zerosPendents = input.remove(0);
					mat[filaZigZag][colZigZag] = 0;
					zerosPendents--;
				} else {
					mat[filaZigZag][colZigZag] = actual;
				}
			} else {
				mat[filaZigZag][colZigZag] = 0;
				zerosPendents--;
			}

			calculaSeguentPosZigZag();
			posicionsEscrites++;
		}
	}
}
