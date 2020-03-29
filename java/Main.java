import auxiliary.Point;
import display.Plotter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import voronoi.VoronoiDiagram;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.Event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Willem Paul
 */
public class Main extends Application
{
	private static final int WINDOW_WIDTH = 750;
	private static final int WINDOW_HEIGHT = 750;
	private double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
	private double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		List<String> parameters = getParameters().getRaw();

		if (parameters.size() != 2)
			throw new Exception("Please specify an input file and whether or not the resulting Voronoi diagram should be displayed");

		Set<Event> events = readInputFile(parameters.get(0));
		boolean display = Boolean.parseBoolean(parameters.get(1));

		Group root = new Group();
		Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
		Plotter plotter = new Plotter(canvas.getWidth(), canvas.getHeight(), canvas.getGraphicsContext2D(), minX, maxX, minY, maxY);

		if (display)
		{
			plotter.plotSiteEvents(events);
			root.getChildren().add(canvas);
			primaryStage.setTitle("Voronoi Diagram");
			primaryStage.setResizable(false);
			primaryStage.setScene(new Scene(root));
		}

		DoublyConnectedEdgeList voronoiDiagram = new VoronoiDiagram(events);
		writeOutputFile(voronoiDiagram);

		if (display)
		{
			plotter.plotDCEL(voronoiDiagram);
			primaryStage.show();
		}
		else Platform.exit();
	}

	/**
	 * Reads the input points from the specified file, creates object representations of them, and returns a
	 * {@code List} of the points. Also calculates the minimum and maximum x- and y-values of the input set for display
	 * purposes.
	 *
	 * @param filePath The path to the file containing the input points.
	 * @return A {@code List} of {@code Event}s created from the points contained in the given file.
	 */
	private Set<Event> readInputFile(String filePath)
	{
		Set<Event> sites = new HashSet<>();

		try
		{
			File inputFile = new File(filePath);
			Scanner scanner = new Scanner(inputFile);

			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine().trim();
				if (line.isEmpty() || line.charAt(0) == '#') continue;

				StringTokenizer tokenizer = new StringTokenizer(line, ") (");

				int i = 0;
				double[] coordinates = new double[2];
				while (tokenizer.hasMoreTokens())
				{
					coordinates[i++] = Double.parseDouble(tokenizer.nextToken().replaceAll(",", ""));
					if (i % 2 == 0)
					{
						sites.add(new Event(new Point(coordinates[0], coordinates[1])));

						if (coordinates[0] < minX) minX = coordinates[0];
						else if (coordinates[0] > maxX) maxX = coordinates[0];

						if (coordinates[1] < minY) minY = coordinates[1];
						else if (coordinates[1] > maxY) maxY = coordinates[1];

						i = 0;
					}
				}
			}
		}
		catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
		}

		return sites;
	}

	/**
	 * Writes the specified Voronoi diagram to the output file.
	 *
	 * @param voronoi The Voronoi diagram to be written, represented as a {@code DoublyConnectedEdgeList}.
	 */
	private void writeOutputFile(DoublyConnectedEdgeList voronoi)
	{
		try
		{
			File outputFile = new File("voronoi.txt");
			outputFile.createNewFile();

			FileWriter writer = new FileWriter(outputFile);
			writer.write("****** Voronoi Diagram ******\n");
			writer.write(voronoi.toString());
			writer.write("\n\n");
			writer.write("****** Delaunay Triangulation ******\n");
			writer.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}