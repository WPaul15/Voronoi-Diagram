import display.Plotter;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import voronoi.Point;
import voronoi.VoronoiDiagram;
import voronoi.dcel.DoublyConnectedEdgeList;
import voronoi.queue.Event;

import javax.sound.sampled.Line;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author Willem Paul
 */
public class Main extends Application
{
	private static final String INPUT_FILE = "sites.txt";
	private static final int WINDOW_WIDTH = 750;
	private static final int WINDOW_HEIGHT = 750;
	private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
	private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Group root = new Group();
		Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
		Plotter plotter = new Plotter(canvas.getWidth(), canvas.getHeight(), canvas.getGraphicsContext2D());

		List<Event> events = readInputFile(INPUT_FILE);

		plotter.setScale(minX, maxX, minY, maxY);
		plotter.plotSiteEvents(events);
		root.getChildren().add(canvas);

		DoublyConnectedEdgeList voronoiDiagram = new VoronoiDiagram(events);
		writeOutputFile(voronoiDiagram);

		primaryStage.setTitle("Voronoi Diagram");
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	/**
	 * Reads the input points from the specified file, creates object representations of them, and returns a
	 * {@code List} of the points.
	 *
	 * @param filePath The path to the file containing the input points.
	 * @return A {@code List} of {@code Event}s created from the points contained in the given file.
	 */
	private List<Event> readInputFile(String filePath)
	{
		List<Event> sites = new ArrayList<>();

		try
		{
			File inputFile = new File(filePath);
			Scanner scanner = new Scanner(inputFile);

			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				StringTokenizer tokenizer = new StringTokenizer(line, ") (");

				int i = 0;
				int[] coordinates = new int[2];
				while (tokenizer.hasMoreTokens())
				{
					coordinates[i++] = Integer.parseInt(tokenizer.nextToken().replaceAll(",", ""));
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