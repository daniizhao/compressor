JAVAS = ./*.java ./presentacio/*.java ./domini/*.java ./dades/*.java

CLASSES = ./*.class ./presentacio/*.class ./domini/*.class ./dades/*.class

all: CreaJar

CreaJar: CompilaClasses
	echo "Main-Class: Main" > MANIFEST.MF
	jar cvfm ../EXE/Compressor_Compilat.jar MANIFEST.MF $(CLASSES) ./com/intellij/uiDesigner/core/* ./AjudaPrograma/*

CompilaClasses: $(JAVAS)
	javac $(JAVAS)

clean:
	rm ./*.class ./presentacio/*.class ./domini/*.class ./dades/*.class MANIFEST.MF ../EXE/Compressor_Compilat.jar
