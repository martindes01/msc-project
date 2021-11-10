# msc-project

*Theory and Proof-of-Concept Implementations of Streaming Algorithms for Multiset Data*

## About

This is the source for my dissertation *Theory and Proof-of-Concept Implementations of Streaming Algorithms for Multiset Data* and its corresponding implementation library submitted towards the postgraduate degree of MSc Computer Science at the University of Birmingham in 2021.

## Getting Started

### Prerequisites

It is recommended to use [pdfTeX](http://tug.org/applications/pdftex/) to generate a PDF file directly from the source.
The pdfTeX extension is included in most [free TeX distributions](http://www.tug.org/interest.html#free).

It is recommended to use the provided Gradle 7.1.1 Wrappers (`gradlew` on Unix and `gradlew.bat` on Windows) to build and test the library.
Although this method does not require a Gradle installation, it does require the installation of a version of the [Java Development Kit](https://adoptopenjdk.net/) between 11 and 16, inclusive.
It is assumed that the `JAVA_HOME` and `PATH` environment variables are correctly set.

Running the test tasks on Windows requires the `hadoop.dll` and `winutils.exe` binary files corresponding to Apache Hadoop version 3.2 to be available on the system path.
These are available [here](https://github.com/cdarlint/winutils).
Additionally, the `HADOOP_HOME` environment variable should be set such that `HADOOP_HOME\bin\winutils.exe` correctly identifies the `winutils.exe` executable.

### Installation

Simply clone the source from this repository

```shell
git clone https://github.com/martindes01/msc-project.git
cd msc-project
```

### Dissertation

The `./docs` directory contains the LaTeX source for the project proposal, various presentations and the dissertation.

- Main LaTeX file: [dissertation.tex](docs/dissertation/dissertation.tex)
- Compiled PDF: [dissertation.pdf](../../releases/latest/download/dissertation.pdf)

### Library

The `./src` directory contains the implementation library.
This follows the standard Gradle project structure for a Java library.

The project settings file `./src/settings.gradle` declares the existence of the library subproject in the `./src/lib` directory.
This contains two source sets, whose structures follow the package hierarchy.

The `main` source set consists of the `summaries` implementation package, the `wrappers.spark` convenience package and a `utils` package that comprises the `Math` utility class, the `HashFunction` functional interface and its `LinearHashFunction` implementation.

The build script `./src/lib/build.gradle` defines the dependencies and build tasks of the project, including the tasks used to evaluate the summary implementations.

## Usage

### Dissertation

To generate the PDF, run pdfTeX with the command `pdflatex` to use the standard LaTeX macros, followed by BibTeX with the command `bibtex` to generate the citations and bibliography.
A final two passes of pdfTeX are needed to generate all features of the document.

```shell
cd docs/dissertation
pdflatex dissertation.tex
bibtex dissertation.tex
pdflatex dissertation.tex
pdflatex dissertation.tex
```

### Library

The list of build tasks can be obtained by running `./gradlew tasks` from the `./src` directory.
Each of the tasks listed can be run using `./gradlew <task>`.

```shell
cd src
./gradlew tasks
./gradlew <task>
```

The library can be compiled by running the `build` task, and comprehensive documentation of the public API can be generated by running the `javadoc` task.
This will write the generated documentation to `./src/lib/build/docs/javadoc`.

The available test tasks are `testFingerprintAccuracy`, `testFingerprintPerformance`, `testCountSketchAccuracy`, `testCountSketchPerformance`, `testDyadicCountSketchAccuracy` and `testDyadicCountSketchPerformance`.
Each test task can be run using the following command-line instruction, which takes a space-delimited list of key&ndash;value options in the form `<option-key>=<option-value>`.

```shell
./gradlew <test> [--project-prop options="<options>"]
```

While all options have a default value, some of these are specific to individual summaries.
Note that since the test tasks were developed solely for the generation of results for the dissertation, no sanitisation or error checking is performed on the command-line options.
To avoid errors, ensure that there are no extraneous spaces and that each numerical value is given in a format recognised by `Integer.parseInt`.

Option key | Type | Description | Global default value
--- | --- | --- | ---:
`--directory` | `String` | The directory against which paths are resolved | `""`
`--name` | `String` | The prefix to be used in generated filenames |
`--delimiter` | `String` | The coordinate delimiter in the existing or generated dataset | `,`
`--input` | `String` | The path to an existing dataset to be used in a performance test | `""`
`--size` | `int` | The number of item&ndash;weight pairs in the generated dataset | `65536`
`--item-lower-bound` | `int` | The lower bound of the universe from which items are drawn, inclusive |
`--item-lower-bound` | `int` | The upper bound of the universe from which items are drawn, inclusive |
`--weight-lower-bound` | `int` | The lower bound of the interval from which weights are drawn, inclusive |
`--weight-lower-bound` | `int` | The upper bound of the interval from which weights are drawn, inclusive |
`--rows` | `int` | The number of rows to be used in the constructed sketch |
`--columns` | `int` | The number of columns to be used in the constructed sketch |
`--queries` | `int` | The number of item-specific queries to perform on the constructed summary | `65536`
`--runs` | `int` | The number of times to run the test | `1`

## License

This project is distributed under the terms of version 3 of the GNU General Public License as published by the Free Software Foundation.
See [COPYING](COPYING) for more information.
