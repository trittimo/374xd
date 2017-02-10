# UMLGenerator

*Project by Andrew McKee and Michael Trittin*

## UML Diagram

![UMLDiagram](ProjectUML.png "UML Diagram for this Project")

## Running the program

The program can be run with command line arguments to set certain paramters. Concrete IAnalyzers added to the system can also see these arguments, and make decisions based on that information. There are four types of arguments.

1. **Flags**: Single arguments that begin with a dash. They represent setting a toggleable boolean option. For example, if the `-f` argument is passed, then the system will recognize this as the `f` flag being set to `true`. The absense of such a flag is implies the flag should be set to `false`.
2. **Key-Value Pairs**: Key-Value pairs set a the value of a key in a map. The system will treat any argument that starts with two dashes as a key, and the next argument as the value. For example, `--maxnodes 5` would set the store `5` under `maxnodes` in the key-value pair map.
3. **Named lists**: A list of values that is stored under a name. This is a special type of argument, as only two named lists that are recognized are `whitelist` and `blacklist`. This is only set when using a `.xml` config properties file. Named lists maps an id `string` to a `List<String>`
4. **Arguments**: Generic arguments that do not fit any of the above categories. In our implementation of `ICMDHandler`, every argument is treated as class to include in the diagram.

### Command Line Arguments

These are the default command line arguments recognized by the `DefaultCMDHandler` implementation of `ICMDHandler`:

##### Flags
* `r`: Enable recursive mode (adds `RecursiveClassAnalyzer` to `IAnalyzer` list)
* `f`: Enable field analysis (adds `FieldAnalyzer` to `IAnalyzer` list)
* `s`: Enable signature analysis (adds `SignatureAnalyzer` to `IAnalyzer` list)
* `m`: Enable method-body analysis (adds `MethodBodyAnalyzer` to `IAnalyzer` list)
##### Key-Value Pairs
* `analyzers`: Comma seperated list of `IAnalyzer` implementations to add to the `IAnalyzer` list.
* `lastpass`: Comma seperated list of `IAnalyzer` implementations to add to the `lastpass` `IAnalyzer` list. The `lastpass` list is run after all the normal `IAnalyzer` list has finished.
* `ECI`: The location of the External Class Index XML
##### Named Lists
* `whitelist`: Whitelist of classes for the UML
* `blacklist`: Blacklist of classes for the UML

### Config File

The config file uses the [XML format for Java Properties](https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html). Pairs can be specified with the pairs key, flags with the flags key, and options with the options key. Whitelist and blacklist also have their own key.

There is also a eci.xml that is used for loading classes off the class path. However, it is recommended that you use the java VM classpath argument instead wherever possible, as this behavior is rather glitchy. 