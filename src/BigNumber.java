import java.util.*;

public class BigNumber {
    private final List<Integer> mDigits; // digits in little endian form
    private final boolean mIsPositive;

    public BigNumber(String number) {
        int firstDigitIndex = 0;
        if (number.charAt(0) == '-') {
            mIsPositive = false;
            firstDigitIndex = 1;
        } else {
            mIsPositive = true;
        }
        mDigits = new ArrayList<>();
        for (int i = number.length() - 1; i >= firstDigitIndex; i--) {
            mDigits.add((int) number.charAt(i) - 48);
        }
    }

    public BigNumber(int number) {
        mIsPositive = number >= 0;
        if (!mIsPositive) {
            number *= -1;
        }
        if (number == 0) {
            mDigits = new ArrayList<>(Collections.singletonList(0));
            return;
        }

        mDigits = new ArrayList<>();
        while (number > 0) {
            int digit = number % 10;
            mDigits.add(digit);
            number /= 10;
        }
    }

    private BigNumber(List<Integer> digitsLittleEndian) {
        this(digitsLittleEndian, true);
    }

    private BigNumber(List<Integer> digitsLittleEndian, boolean isPositive) {
        this.mIsPositive = isPositive;
        if (digitsLittleEndian.isEmpty()) {
            mDigits = new ArrayList<>(Collections.singletonList(0));
            return;
        }

        if (!verifyDigitsListIsValid(digitsLittleEndian)) {
            throw new IllegalArgumentException("Each digit must be less than 10.");
        }
        mDigits = digitsLittleEndian;
    }

    private static boolean verifyDigitsListIsValid(List<Integer> digitsLittleEndian) {
        for (int digit : digitsLittleEndian) {
            if (digit / 10 > 0) {
                return false;
            }
        }
        return true;
    }

    public int length() {
        return mDigits.size();
    }

    public int getDigitAt(int index) {
        return index < mDigits.size() ? mDigits.get(index) : 0;
    }

    public BigNumber add(BigNumber other) {
        return add(this, other);
    }

    public BigNumber plus(BigNumber other) {
        return add(other);
    }

    public BigNumber minus(BigNumber other) {
        return subtract(other);
    }

    public boolean isPositive() {
        return mIsPositive;
    }

    public boolean isNegative() {
        return !mIsPositive;
    }

    public BigNumber absoluteValue() {
        return new BigNumber(new ArrayList<>(mDigits), true);
    }

    public BigNumber negate() {
        return new BigNumber(new ArrayList<>(mDigits), !mIsPositive);
    }

    public static BigNumber add(BigNumber numberOne, BigNumber numberTwo) {
        if (numberOne.isPositive() && numberTwo.isNegative()) {
            // if y = -|y|,
            // x + y = x - |y|
            return subtract(numberOne, numberTwo.negate());
        }
        if (numberOne.isNegative() && numberTwo.isPositive()) {
            // if x = -|x|,
            // x + y = -|x| + y = y - |x|
            return subtract(numberTwo, numberOne.negate());
        }

        List<Integer> digits = new ArrayList<>();
        int maxDigits = Math.max(numberOne.length(), numberTwo.length());
        int overflow = 0;
        for (int i = 0; i < maxDigits; i++) {
            int sum = numberOne.getDigitAt(i) + numberTwo.getDigitAt(i) + overflow;
            int newDigit = sum % 10;
            digits.add(newDigit);
            overflow = sum / 10;
        }
        if (overflow > 0) {
            digits.add(overflow);
        }

        return new BigNumber(digits, numberOne.isPositive() && numberTwo.isPositive());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (! (other instanceof  BigNumber)) {
            return false;
        }

        return equals(this, (BigNumber) other);
    }

    public static boolean equals(BigNumber numberOne, BigNumber numberTwo) {
        return numberOne.length() == numberTwo.length()
                && numberOne.isPositive() == numberTwo.isPositive()
                && numberOne.mDigits.equals(numberTwo.mDigits);
    }

    public static boolean greaterThan(BigNumber numberOne, BigNumber numberTwo) {
        if (numberOne.isPositive() && numberTwo.isNegative()) {
            return true;
        }
        if (numberOne.isNegative() && numberTwo.isPositive()) {
            return false;
        }
        if (numberOne.length() > numberTwo.length()) {
            return true;
        }
        if (numberTwo.length() > numberOne.length()) {
            return false;
        }
        for (int i = numberOne.length() - 1; i >= 0; i--) {
            if (numberOne.getDigitAt(i) > numberTwo.getDigitAt(i)) {
                return true;
            }
            if (numberTwo.getDigitAt(i) > numberOne.getDigitAt(i)) {
                return false;
            }
        }
        return false;
    }

    public boolean greaterThan(BigNumber other) {
        return greaterThan(this, other);
    }

    public static BigNumber subtract(BigNumber numberOne, BigNumber numberTwo) {
        if (numberOne.equals(numberTwo)) {
            return new BigNumber(0);
        }
        if (numberOne.isNegative() && numberTwo.isPositive()) {
            // if x = -|x|,
            // x - y = -|x| - y = - (|x| + y)
            return numberOne.absoluteValue().add(numberTwo).negate();
        }
        if (numberOne.isPositive() && numberTwo.isNegative()) {
            // if y = -|y|,
            // x - y = x - (-|y|) = x + y
            return numberOne.add(numberTwo);
        }
        if (numberOne.isNegative() && numberTwo.isNegative()) {
            // if x = -|x| and y = -|y|,
            // x - y = -|x| - (-|y|) = |y| - |x|
            return numberTwo.absoluteValue().subtract(numberOne.absoluteValue());
        }
        if (numberTwo.greaterThan(numberOne)) {
            // x - y = -(y - x)
            return subtract(numberTwo, numberOne).negate();
        }
        // Now we have guaranteed that numberOne > numberTwo
        List<Integer> digits = new ArrayList<>();
        boolean borrowedFromNextDigit = false;
        for (int i = 0; i < numberOne.length(); i++) {
            int currentDigit = numberOne.getDigitAt(i);
            if (borrowedFromNextDigit) {
                currentDigit--;
            }
            int difference = currentDigit - numberTwo.getDigitAt(i);
            if (difference < 0) {
                borrowedFromNextDigit = true;
                difference += 10;
            } else {
                borrowedFromNextDigit = false;
            }
            digits.add(difference);
        }
        if (digits.get(digits.size() - 1) == 0) {
            digits.remove(digits.size() - 1);
        }
        return new BigNumber(digits, true);
    }

    public BigNumber subtract(BigNumber other) {
        return subtract(this, other);
    }

    private BigNumber getDigitsAtAndAboveNthPower(int n) {
        if (n > mDigits.size()) {
            return new BigNumber(0);
        }
        return new BigNumber(mDigits.subList(n, mDigits.size()), mIsPositive);
    }

    private BigNumber getDigitsBelowNthPower(int n) {
        if (n > mDigits.size()) {
            return this;
        }
        return new BigNumber(mDigits.subList(0, n), mIsPositive);
    }

    public BigNumber multiply(BigNumber other) {
        return multiply(this, other);
    }

    public boolean isNotZero() {
        return !isZero();
    }

    public boolean isZero() {
        return hasOneDigit() && mDigits.get(0) == 0;
    }

    public boolean hasOneDigit() {
        return mDigits.size() == 1;
    }

    public BigNumber times(BigNumber other) {
        return multiply(other);
    }

    public static BigNumber multiply(BigNumber numberOne, BigNumber numberTwo) {
        if (numberOne.isNegative() && numberTwo.isPositive()) {
            return numberOne.absoluteValue().multiply(numberTwo).negate();
        }
        if (numberOne.isPositive() && numberTwo.isNegative()) {
            return numberOne.multiply(numberTwo.absoluteValue()).negate();
        }
        if (numberOne.isNegative() && numberTwo.isNegative()) {
            return numberOne.absoluteValue().multiply(numberTwo.absoluteValue());
        }

        if (numberOne.isZero() || numberTwo.isZero()) {
            return new BigNumber(0);
        }

        if (numberOne.hasOneDigit() && numberTwo.hasOneDigit()) {
            return new BigNumber(numberOne.mDigits.get(0) * numberTwo.mDigits.get(0));
        }

        int n = Math.max(numberOne.length(), numberTwo.length())/2;

        BigNumber a = numberOne.getDigitsAtAndAboveNthPower(n);
        BigNumber b = numberOne.getDigitsBelowNthPower(n);
        BigNumber c = numberTwo.getDigitsAtAndAboveNthPower(n);
        BigNumber d = numberTwo.getDigitsBelowNthPower(n);

        BigNumber ac = a.multiply(c);
        BigNumber bd = b.multiply(d);
        BigNumber f = (a.add(b)).multiply(c.add(d));
        BigNumber g = f.subtract(ac).subtract(bd);

        BigNumber firstPartialSum = ac.multiplyByTen(2*n);
        BigNumber secondPartialSum = g.multiplyByTen(n);

        return firstPartialSum.plus(secondPartialSum).plus(bd);
    }

    public BigNumber multiplyByTen(int n) {
        return shiftRight(n);
    }

    public BigNumber shiftRight(int n) {
        if (isZero()) {
            return this;
        }
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            digits.add(0);
        }
        digits.addAll(mDigits);
        return new BigNumber(digits, mIsPositive);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        ListIterator iterator = mDigits.listIterator(mDigits.size());

        if (isNegative()) {
            stringBuilder.append('-');
        }

        while (iterator.hasPrevious()) {
            stringBuilder.append(iterator.previous());
        }
        return stringBuilder.toString();
    }
}
