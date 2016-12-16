package generator.exporters;

import java.io.File;
import java.io.IOException;

import generator.Graph;
import generator.commands.CMDParams;

public class PNGExporter implements IExporter {

	private String default_cmd;
	
	public PNGExporter(String s) {
		default_cmd = s;
	}
	
	@Override
	public void export(Graph graph, CMDParams params) {
		String pngOut = params.getOptionPairs().get("png");
		if (pngOut == null)
			return;
		String inputFile = params.getOptionPairs().get("out");
		String graphVizCMD = params.getOptionPairs().get("gviz");
		if (graphVizCMD == null)
			graphVizCMD = default_cmd;
		String[] commands = {
					default_cmd,
					"-Tpng",
					inputFile
			};
		ProcessBuilder pbuild = new ProcessBuilder(commands);
		//pbuild.directory(new File(graphVizCMD));
		pbuild.redirectOutput(new File(pngOut));
		try {
			Process p = pbuild.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
