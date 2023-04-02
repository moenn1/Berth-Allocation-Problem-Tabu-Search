package Solution;

import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import static Solution.Main.arrivalTime;

public class Main {
    public static int nBerths, nShips;
    public static int[][] inputMatrix;

    public static int[] arrivalTime;


    void initSolution(Solution s)
    {
        int j;
        Random r = new Random();

        s.cost = 0;
        for (int i = 0; i < nShips; i++)
        {
            do
            {
                j = r.nextInt(nBerths);
            }
            while (inputMatrix[j][i] == 0);

            s.MAT[j][s.nbShipsOnBerth[j]] = i;
            s.nbShipsOnBerth[j]++;
        }

        for (int i = 0; i < nBerths; i++)
        {
            bubbleSort(s.MAT[i], s.nbShipsOnBerth[i]);
        }
    }

    void genRandomSol(Solution s)
    {
        int pship, bship, berthN, ship;


        do
            bship = (int) (Math.random() * nBerths);
        while (s.nbShipsOnBerth[bship] == 0);

        pship = (int) (Math.random() * s.nbShipsOnBerth[bship]);

        ship = s.MAT[bship][pship];

        do
            berthN = (int) (Math.random() * nBerths);
        while (inputMatrix[berthN][ship] == 0 || bship == berthN);


        for (int j = pship; j < s.nbShipsOnBerth[bship]; j++)
        {
            s.MAT[bship][j] = s.MAT[bship][j + 1];
        }
        s.nbShipsOnBerth[bship]--;

        s.MAT[berthN][s.nbShipsOnBerth[berthN]] = ship;
        s.nbShipsOnBerth[berthN]++;

        bubbleSort(s.MAT[berthN], s.nbShipsOnBerth[berthN]);

        s.costFunction();
    }



    void bubbleSort(int row[], int qtd)
    {
        int flag, aux;
        flag = 1;

        while (flag == 1)
        {
            flag = 0;
            for (int j = 0; j < qtd - 1; j++)
            {

                if (arrivalTime[row[j]] > arrivalTime[row[j + 1]])
                {
                    flag = 1;
                    aux = row[j];
                    row[j] = row[j + 1];
                    row[j + 1] = aux;
                }
            }
        }
    }

  //Tabu search
    void tabuSearch(int iterMax, int tabuMax, int maxNoImprove)
    {
        int iter, tabu, noImprove;
        int i, j, k, l;
        int cost, aux;
        int bestCost;
        int bestI, bestJ, bestK, bestL;
        int[][] tabuList;

        tabuList = new int[nBerths][nBerths];

        for (i = 0; i < nBerths; i++)
        {
            for (j = 0; j < nBerths; j++)
            {
                tabuList[i][j] = 0;
            }
        }

        iter = 0;
        tabu = 0;
        noImprove = 0;

        Solution s = new Solution();
        initSolution(s);
        s.costFunction();
        bestCost = s.cost;
        while (iter < iterMax && noImprove < maxNoImprove)
        {
            genRandomSol(s);
            bestI = -1;
            bestJ = -1;
            bestK = -1;
            bestL = -1;
            cost = Integer.MAX_VALUE;

            for (i = 0; i < nBerths; i++)
            {
                for (j = 0; j < nBerths; j++)
                {
                    for (k = 0; k < s.nbShipsOnBerth[i]; k++)
                    {
                        for (l = 0; l < s.nbShipsOnBerth[j]; l++)
                        {
                            if (tabuList[i][j] <= iter)
                            {
                                aux = s.cost;
                                aux -= arrivalTime[s.MAT[i][k]];
                                if (k != 0)
                                {
                                    aux -= arrivalTime[s.MAT[i][k - 1]];
                                }
                                if (k != s.nbShipsOnBerth[i] - 1)
                                {
                                    aux -= arrivalTime[s.MAT[i][k + 1]];
                                }

                                aux -= arrivalTime[s.MAT[j][l]];
                                if (l != 0)
                                {
                                    aux -= arrivalTime[s.MAT[j][l - 1]];
                                }
                                if (l != s.nbShipsOnBerth[j] - 1)
                                {
                                    aux -= arrivalTime[s.MAT[j][l + 1]];
                                }

                                aux += arrivalTime[s.MAT[i][k]];
                                if (l != 0)
                                {
                                    aux += arrivalTime[s.MAT[j][l - 1]];
                                }
                                if (l != s.nbShipsOnBerth[j] - 1)
                                {
                                    aux += arrivalTime[s.MAT[j][l + 1]];
                                }

                                aux += arrivalTime[s.MAT[j][l]];
                                if (k != 0)
                                {
                                    aux += arrivalTime[s.MAT[i][k - 1]];
                                }

                                if (k != s.nbShipsOnBerth[i] - 1)
                                {
                                    aux += arrivalTime[s.MAT[i][k + 1]];
                                }

                                if (aux < cost)
                                {
                                    cost = aux;
                                    bestI = i;
                                    bestJ = j;
                                    bestK = k;
                                    bestL = l;
                                }

                            }
                        }
                    }
                }
            }

            if (bestI != -1)
            {
                aux = s.MAT[bestI][bestK];
                s.MAT[bestI][bestK] = s.MAT[bestJ][bestL];
                s.MAT[bestJ][bestL] = aux;
                s.cost = cost;
                tabuList[bestI][bestJ] = iter + tabuMax;
                tabu++;
                if (s.cost < bestCost)
                {
                    bestCost = s.cost;
                    noImprove = 0;
                }
                else
                {
                    noImprove++;
                }
            }
            for (int L=0; L<nBerths; L++)
            {
                for (int K=0; K<s.nbShipsOnBerth[L]; K++)
                {

                        System.out.print(s.MAT[L][K] + " ");
                }
            }
            System.out.println();
            System.out.println("The cost of this solution is: " + s.cost);
            iter++;
        }

        Data.printSolution(s);
    }


    public static void main(String[] args)  {
        int nbShips, nbBerths;
        String fileDir1, fileDir2;
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the number of ships: ");
        nbShips = scan.nextInt();
        System.out.println("Enter the number of berths: ");
        nbBerths = scan.nextInt();
        //System.out.println("Enter the file directory: ");
        fileDir2 = "src\\Solution\\datesarrivee.csv";
        fileDir1 = "src\\Solution\\dureedeservice.csv";
        Main main = new Main();
        Data data = new Data();
        data.readData(fileDir1, fileDir2, nbBerths, nbShips);
        data.printData();
        System.out.println();
        System.out.println("Enter the number of iterations: ");
        int N = scan.nextInt();
        main.tabuSearch(N, 1000,1000);
    }
}




class Solution{
    int MAT[][];
    int nbShipsOnBerth[];

    int cost;

    public Solution(){
        MAT = new int[Main.nBerths][Main.nShips];
        nbShipsOnBerth = new int[Main.nBerths];
        cost = 0;
    }

    public void costFunction() {
        int cost = 0;
        //calculate cost function
        for (int i = 0; i < Main.nBerths; i++) {
            for (int j = 0; j < nbShipsOnBerth[i]; j++) {
                cost += Main.inputMatrix[i][MAT[i][j]];
            }
        }

        this.cost = cost;
        //System.out.println(cost);
    }

}