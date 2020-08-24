package net.myfirst.webapp.datastructures;

public class SortApp {
    public static void main(String[] args) {
        int[] numbers = {5, 2, 3, 4, 1};

//        int firstNumber = numbers[0];
//        numbers[0] = numbers[4];
//        numbers[4] = firstNumber;


        int counter = -1;

        for (int i = 0; i < numbers.length; i++) {
            int currentNumber = numbers[i];

            if (i+1 < numbers.length) {
                int nextNumber = numbers[i + 1];

                if (currentNumber > nextNumber) {
                    numbers[i] = nextNumber;
                    numbers[i+1] = currentNumber;
                }
            }

            //System.out.println(numbers[i]);
        }

        for (int i = 0; i < numbers.length; i++) {
            System.out.println(numbers[i]);
        }
    }
}
