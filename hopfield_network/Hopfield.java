import java.util.*;
import java.io.*;

public class Hopfield
{
	public static void main(String[] args)
	{
		String kill = "";
		String ans = "p";
		String inputName = "";
		String outputName = "";
		int[][] weights = new int[0][0];
		boolean trained = false;
		int numtest = 0, dimcol = 0, dimrow = 0, dim = 0;
		
		Scanner kb = new Scanner(System.in);
		
		while(ans != "quit")
		{
			System.out.flush();
			System.out.println("действия:");
			System.out.println("'train' - обучить сеть");
			System.out.println("'load' - загрузить веса");
			System.out.println("'save' - сохранить веса");
			System.out.println("'test' - проверить сеть");
			System.out.println("'quit' - выйти");
			ans = kb.next().toLowerCase();
			switch (ans){
				case "train":
					System.out.println("___");
					System.out.println("название файла для обучения сети:");
					System.out.println("___");
					inputName = kb.next();
					int[][] tempWeights = new int[0][0];
					try
					{
						Scanner in = new Scanner(new File(inputName));
						dimrow = in.nextInt();
						dimcol = in.nextInt();
						numtest = in.nextInt();
						dim = dimrow * dimcol;
						kill = in.nextLine();
						tempWeights = new int[numtest][dim];
						for(int count = 0; count < numtest; count++)
						{
							kill = in.nextLine();
							int tempIndex = 0;
							for(int j = 0; j < dimcol; j++)
							{
								String tempString = in.nextLine();
								for(int i = 0; i < dimrow; i++)
								{
									char tempChar = tempString.charAt(i);
									if(tempChar == ' ')
										tempWeights[count][tempIndex] = -1;
									else
										tempWeights[count][tempIndex] = 1;
									tempIndex++;
								}
							}
						}
						in.close();
					}
					catch(FileNotFoundException e)
					{
						System.out.println(e.getMessage());
						System.exit(1);
					}
					catch(NoSuchElementException e)
					{
						System.out.println(e.getMessage());
						System.exit(1);
					}
					weights = new int[dim][dim];
					for(int j = 0; j < dim; j++)
					{
						for(int i = 0; i < dim; i++)
							weights[j][i] = 0;
					}
					int[][] tempWeights1 = new int[1][dim];
					int[][] tempWeights2 = new int[dim][1];
					int[][] tempWeights3 = new int[dim][dim];
					for(int count = 0; count < numtest; count++)
					{
						for(int i = 0; i < dim; i++)
						{
							tempWeights1[0][i] = tempWeights[count][i];
							tempWeights2[i][0] = tempWeights[count][i];
						}
						tempWeights3 = multiplyMatrices(tempWeights2, tempWeights1);
						weights = addMatrices(weights, tempWeights3);
					}
					System.out.println("___");
					System.out.println("сеть обучена!");
					System.out.println("___");
					trained = true;
					break;
				case "load":
					System.out.println("___");
					System.out.println("название файла с весами:");
					System.out.println("___");
					inputName = kb.next();
					try
					{
						Scanner in = new Scanner(new File(inputName));
						dimrow = in.nextInt();
						dimcol = in.nextInt();
						dim = dimrow * dimcol;
						weights = new int[dim][dim];
						for(int j = 0; j < dim; j++)
						{
							for(int i = 0; i < dim; i++)
								weights[j][i] = in.nextInt();
						}
						in.close();
					}
					catch(FileNotFoundException e)
					{
						System.out.println(e.getMessage());
						System.exit(1);
					}
					System.out.println("___");
					System.out.println("веса загружены!");
					System.out.println("___");
					trained = true;
					break;
				case "save":
					if(trained == true)
					{
						System.out.println("___");
						System.out.println("название файла, куда вывести веса:");
						System.out.println("___");
						outputName = kb.next();
						try
						{
							PrintWriter out = new PrintWriter(outputName);
							out.println(dimrow);
							out.println(dimcol);
							for(int j = 0; j < dim; j++)
							{
								for(int i = 0; i < dim; i++)
								{
									out.print(weights[j][i] + " ");
								}

								out.println("");
							}
							out.close();
						}
						catch(IOException e)
						{
							System.out.println(e);
							System.exit(1);
						}
						System.out.println("___");
						System.out.println("веса сохранены: " + outputName);
						System.out.println("___");
					}
					else
					{
						System.out.println("___");
						System.out.println("сначала нужно обучить сеть");
						System.out.println("___");
					}
					break;
				case "test":
					if(trained == true)
					{
						System.out.println("___");
						System.out.println("название файла для проверки:");
						System.out.println("___");
						inputName = kb.next();
						int[][] testingWeights = new int[0][0];
						try
						{
							Scanner in = new Scanner(new File(inputName));
							dimrow = in.nextInt();
							dimcol = in.nextInt();
							numtest = in.nextInt();
							dim = dimrow * dimcol;
							kill = in.nextLine();
							testingWeights = new int[numtest][dim];
							for(int count = 0; count < numtest; count++)
							{
								kill = in.nextLine();
								int tempIndex = 0;
								for(int j = 0; j < dimcol; j++)
								{
									String tempString = in.nextLine();
									for(int i = 0; i < dimrow; i++)
									{
										char tempChar = tempString.charAt(i);
										if(tempChar == ' ')
											testingWeights[count][tempIndex] = -1;
										else
											testingWeights[count][tempIndex] = 1;
										tempIndex++;
									}
								}
							}
							in.close();
						}
						catch(FileNotFoundException e)
						{
							System.out.println(e.getMessage());
							System.exit(1);
						}
						catch(NoSuchElementException e)
						{
							System.out.println(e.getMessage());
							System.exit(1);
						}
						int[] randomUpdate = new int[dim];
						boolean converged = false;
						double yin = 0;
						int y = 0;
						int epochs = 0;

						int[][] origTesting = new int[numtest][dim];
						for(int count = 0; count < numtest; count++)
						{
							for(int i = 0; i < dim; i++)
								origTesting[count][i] = testingWeights[count][i];
						}

						while(!converged)
						{
							epochs++;
							converged = true;

							for(int count = 0; count < numtest; count++)
							{
								for(int k = 0; k < dim; k++)
									randomUpdate[k] = k;
								randomUpdate = randomPermutation(randomUpdate);
								for(int k = 0; k < dim; k++)
								{
									yin = testingWeights[count][randomUpdate[k]];
									for(int i = 0; i < dim; i++)
										yin += testingWeights[count][i] * weights[randomUpdate[k]][i];
									if(yin > 0)
										y = 1;
									else if(yin < 0)
										y = -1;
									if(y != testingWeights[count][randomUpdate[k]])
									{
										converged = false;
										testingWeights[count][randomUpdate[k]] = y;
									}
								}
							}
						}
						System.out.println("___");
						System.out.println("обучение заняло " + epochs + " эпохи");
						System.out.println("название файла, куда вывести результаты:");
						System.out.println("___");
						outputName = kb.next();
						try
						{
							PrintWriter out = new PrintWriter(outputName);
							int counter = 0;
							int counter2 = 0;
							for(int count = 0; count < numtest; count++)
							{
								out.println("пример " + (count + 1) + ":");
								out.println("");
								counter = 0;
								counter2 = 0;
								for(int j = 0; j < dimcol; j++)
								{
									for(int i = 0; i < dimrow; i++)
									{
										if(origTesting[count][counter] == 1)
											out.print("0");
										else
											out.print(" ");
										counter++;
									}
									out.print("          ->          ");
									for(int i = 0; i < dimrow; i++)
									{
										if(testingWeights[count][counter2] == 1)
											out.print("0");
										else
											out.print(" ");
										counter2++;
									}
									out.println("");
								}
								out.println("");
								out.println("-------------------------");
								out.println("");
							}
							out.close();
						}
						catch (IOException e)
						{
							System.out.println(e.getMessage());
							System.exit(1);
						}
						System.out.println("___");
						System.out.println("результаты выгружены в файл: " + outputName);
						System.out.println("___");
					}
					else
					{
						System.out.println("___");
						System.out.println("сначала нужно обучить сеть или загрузить готовые веса");
						System.out.println("___");
					}
					break;
				case "quit":
					System.exit(1);
					break;
				default:
					System.out.println("___");
					System.out.println("такого действия не предусмотрено");
					System.out.println("___");
					break;
			}
		}
	}
	public static int[][] addMatrices(int[][] a, int[][] b)
	{
		int aRows = a.length, aColumns = a[0].length, 
			bRows = b.length, bColumns = b[0].length;

		if (aRows != bRows || aColumns != bColumns)
			throw new IllegalArgumentException("Incompatible for addition");

		int[][] sum = new int[aRows][bColumns];
		
		for (int i = 0; i < aRows; i++)
		{
			for (int j = 0; j < aColumns; j++)
				sum[i][j] = a[i][j] + b[i][j];
		}

		return sum;
	}
	public static int[][] multiplyMatrices(int[][] a, int[][] b)
	{
		int aRows = a.length, aColumns = a[0].length, 
			bRows = b.length, bColumns = b[0].length;

		if (aColumns != bRows)
			throw new IllegalArgumentException("Incompatible for multiplication");

		int[][] product = new int[aRows][bColumns];

		for (int i = 0; i < aRows; i++)
		{
			for (int j = 0; j < bColumns; j++)
			{
				if (i == j)
					product[i][j] = 0;
				else
				{
					for (int k = 0; k < aColumns; k++)
						product[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return product;
	}
	public static int[] randomPermutation(int[] a)
	{
		Random generator = new Random();
		for(int i = 0; i < a.length; i++)
		{
			int r = generator.nextInt(a.length);
			int temp = a[i];
			a[i] = a[r];
			a[r] = temp;
		}
		return a;
	}
}		

