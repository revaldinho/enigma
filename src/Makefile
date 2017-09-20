VPATH=..

.SUFFIXES: .java .class
.java.class:; javac -O -d . $<

all: classes jar

jar: classes Manifest.txt
	jar cvmf Manifest.txt EnigmaApp.jar *class
	mv EnigmaApp.jar ..

classes: \
	EnigmaApp.class \
	EnigmaApplet.class \
	ExampleFileFilter.class \
	OSXAdapter.class \
	PlugBoardTextField.class \
	Stopwatch.class \
	arrayUtils.class \
	enigma.class \
	rotor.class

clean: 
	rm -rf *class *jar
