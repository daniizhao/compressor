package domini;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanJPEG {
    private static final int MIDA = 256;
    private Node arrelArbre;
    private int indexLlegirArbre;

    public byte[] construirArbre(ArrayList<Byte> input) {
        /*
        Pre: 'input' es l'array de bytes que es vol comprimir amb Huffman
        Post: S'ha creat l'arbre que permet obtenir els codis per tots el nombres diferents que conte input.
        El node arrel s'ha guardat a 'arrelArbre' i es retorna per poder-lo incloure al fitxer comprimit.
         */
        //calculem frequencies
        int[] freq = new int[MIDA]; //mida ascii o mida byte
        for (byte b : input) {
            freq[(int)b&0xFF]++;
        }

        //construim arbre binari
        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (int i = 0; i < MIDA; i++) {
            if (freq[i] > 0) {
                queue.add(new Node((byte)i, freq[i], null, null));
            }
        }

        if (queue.size() == 1) {
            queue.add(new Node((byte)-128, 1, null, null));
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node nou = new Node((byte)-128, left.freq + right.freq, left, right);
            queue.add(nou);
        }

        arrelArbre = queue.poll();
        return escriureArbre();
    }

    private byte[] escriureArbre() {
        /*
        Pre: 'arrelArbre' es el node arrel de l'arbre
        Post: S'ha generat un array de bytes que conte l'arbre
         */
        ByteArrayOutputStream result_b = new ByteArrayOutputStream();
        escriure_rec(arrelArbre, result_b);
        return result_b.toByteArray();
    }

    private void escriure_rec (Node actual, ByteArrayOutputStream ba) {
        /*
        Funcio recursiva que utilitza 'escriureArbre()' per tal d'escriure una representacio lineal
        de l'arbre
         */
        ba.write(actual.num);
        if (!actual.isLeaf()) {
            escriure_rec(actual.leftChild, ba);
            escriure_rec(actual.rightChild, ba);
        }
    }

    public void llegirArbre(byte[] input, int index) {
        /*
        Pre: A partir de la posicio input[index] hi ha la representacio d'un arbre
        Post: 'indexLlegirArbre' es igual a la posicio de 'input' on acaba l'arbre i 'arrelArbre' es
        l'arrel de l'arbre que s'ha llegit
         */
        indexLlegirArbre = index;
        arrelArbre = llegeix_rec(input,index);
    }

    private Node llegeix_rec(byte[] input, int meuIndex) {
        /*
        Funcio recursiva que utilitza 'llegirArbre()' per tal de crear un node de l'arbre
         */
        Node left = null;
        Node right = null;
        if (input[meuIndex] == -128) { //canviar per char que simbolitza buit
            left = llegeix_rec(input, ++indexLlegirArbre);
            right = llegeix_rec(input, ++indexLlegirArbre);
        }
        return new Node(input[meuIndex], 1, left, right);
    }

    public byte[] comprimeix(ArrayList<Byte> input) throws IOException {
        /*
        Pre: S'ha cridat previament a 'construirArbre()' i 'input' es el mateix array que s'ha fet servir
        en aquesta crida
        Post: S'ha comprimit el contingut de 'input' amb els codis obtinguts a partir de l'arbre
         */
        //construim taula amb codis
        Map<Byte, String> lookupTable = new HashMap<>();
        creaLookupTable_rec(arrelArbre, "", lookupTable);

        //codificar
        ByteArrayOutputStream baOutput = new ByteArrayOutputStream();
        byte actual = 0;
        byte bitsUsats = 0;
        for (byte b : input) {
            String codi = lookupTable.get(b);
            int longCodi = codi.length();
            for (int i=0; i<longCodi; i++) {
                actual*=2;
                actual = (byte)(actual | (codi.charAt(i) - '0'));
                bitsUsats++;
                if (bitsUsats == 8) {
                    baOutput.write(actual);
                    bitsUsats = 0;
                }
            }
        }
        byte bitsUtilsUltimByte = 8;
        if (bitsUsats != 0) {
            bitsUtilsUltimByte = bitsUsats;
            while (bitsUsats < 8) {
                actual*=2;
                bitsUsats++;
            }
            baOutput.write(actual);
        }

        byte[] aux_baOutput = baOutput.toByteArray();
        baOutput = new ByteArrayOutputStream();
        baOutput.write(bitsUtilsUltimByte);
        baOutput.write(aux_baOutput);
        return baOutput.toByteArray();
    }

    private static void creaLookupTable_rec(Node node, String s, Map<Byte, String> lookupTable) {
        /*
        Funcio recursiva per omplir 'lookupTable' amb el codi corresponent a cada nombre.
         */
        if (!node.isLeaf()) {
            creaLookupTable_rec(node.leftChild, s + '0', lookupTable);
            creaLookupTable_rec(node.rightChild, s + '1', lookupTable);
        } else {
            lookupTable.put(node.num, s);
        }
    }

    public ArrayList<Byte> descomprimeix(byte[] input) {
        /*
        Pre: Previament s'ha fet una crida a 'llegirArbre() i 'input' es el mateix array utilitzat a aquesta crida
        Post: S'ha descomprimit 'input'
         */
        ArrayList<Byte> result = new ArrayList<>();
        byte bitsUtilsUltimByte = input[indexLlegirArbre+1];
        Node current = arrelArbre;
        int indexInput = indexLlegirArbre+3;
        byte byteActual = input[indexLlegirArbre+2];
        int indexUltimByte = input.length;
        int bitsPendents = 8;
        while ((indexInput < indexUltimByte || (indexInput == indexUltimByte && (8-bitsPendents) < bitsUtilsUltimByte)) && bitsPendents != 0) {
            while (!current.isLeaf()) {
                int bit = (byteActual & 0x80)/128;
                if (bit == 1) {
                    current = current.rightChild;
                } else { //bit == 0
                    current = current.leftChild;
                }
                bitsPendents--;
                if (bitsPendents == 0 && indexInput<input.length) {
                    byteActual = input[indexInput];
                    indexInput++;
                    bitsPendents = 8;
                } else {
                    byteActual = (byte) (byteActual * 2);
                }
            }
            result.add(current.num);
            current = arrelArbre;
        }
        return result;
    }

    private static class Node implements Comparable<Node> {
        private byte num;
        private int freq;
        private Node leftChild;
        private Node rightChild;

        private Node(byte num, int freq, Node leftChild, Node rightChild) {
            /* Creadora de la classe Node */
            this.num = num;
            this.freq = freq;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        public boolean isLeaf() {
            /* Retorna cert si el node no te cap fill */
            return (leftChild == null && rightChild == null);
        }

        @Override
        public int compareTo(final Node n) {
            /* Metode per comparar nodes. Util al construir l'arbre a Huffman. */
            int comp = Integer.compare(this.freq, n.freq);
            if (comp != 0) {
                return comp;
            }
            return Integer.compare(this.num, n.num);
        }
    }
}
