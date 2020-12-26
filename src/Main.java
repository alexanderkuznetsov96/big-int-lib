public class Main {
    public static void main(String[] args){
        BigNumber numberOne = new BigNumber("12345");
        BigNumber numberTwo = new BigNumber("67890");
        BigNumber numberThree = numberOne.negate();
        BigNumber numberFour = numberTwo.negate();

        System.out.println(numberOne + " + " + numberTwo + " = " + numberOne.plus(numberTwo));
        System.out.println(numberOne + " + " + numberFour + " = " + numberOne.plus(numberFour));
        System.out.println(numberThree + " + " + numberOne + " = " + numberThree.plus(numberOne));
        System.out.println(numberThree + " + " + numberFour + " = " + numberThree.plus(numberFour));

        System.out.println(numberOne + " - " + numberTwo + " = " + numberOne.minus(numberTwo));
        System.out.println(numberOne + " - " + numberFour + " = " + numberOne.minus(numberFour));
        System.out.println(numberThree + " - " + numberOne + " = " + numberThree.minus(numberOne));
        System.out.println(numberThree + " - " + numberFour + " = " + numberThree.minus(numberFour));

        System.out.println(numberOne + " x " + numberTwo + " = " + numberOne.times(numberTwo));
        System.out.println(numberOne + " x " + numberFour + " = " + numberOne.times(numberFour));
        System.out.println(numberThree + " x " + numberOne + " = " + numberThree.times(numberOne));
        System.out.println(numberThree + " x " + numberFour + " = " + numberThree.times(numberFour));

        BigNumber numberFive = new BigNumber("3141592653589793238462643383279502884197169399375105820974944592");
        BigNumber numberSix = new BigNumber("2718281828459045235360287471352662497757247093699959574966967627");

        System.out.println(numberFive + " x " + numberSix + " = " + numberFive.multiply(numberSix));
    }
}
