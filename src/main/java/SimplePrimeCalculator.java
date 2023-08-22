import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BigIntegerIterator {
    private final List<String> contain = new ArrayList<>(500);

    BigIntegerIterator(int i) {
        contain.add("" + i + "");
    }

    Integer getNumber() {
        return Integer.decode(contain.get(0));
    }
}

public class SimplePrimeCalculator {

    /**
     * Looks up prime numbers in the range of [ 2, args[0] ]
     */
    public static void main(String[] args) {
        for (Integer prime : getPrimes(Integer.parseInt(args[0]))) {
            System.out.print(prime + "\n");
        }
    }

    private static List<Integer> getPrimes(int maxPrime) {
        List<Integer> primeNumbers = Collections.synchronizedList(new LinkedList<>());

        List<BigIntegerIterator> primeNumbersCandidates = Stream.generate(new Supplier<BigIntegerIterator>() {
            int i = 2;
            @Override
            public BigIntegerIterator get() {
                return new BigIntegerIterator(i++);
            }
        }).limit(maxPrime).collect(Collectors.toList());

        // fill out prime numbers candidates
        for (BigIntegerIterator integer : primeNumbersCandidates) {
            primeNumbers.add(integer.getNumber());
        }

        List<Integer> primeNumbersToRemove = Collections.synchronizedList(new LinkedList<>());

        final int threadPoolSize = Math.max(maxPrime / 100, 3000);
        ExecutorService executors = Executors.newFixedThreadPool(threadPoolSize);

        // lookup prime numbers out of all candidates
        synchronized (primeNumbersToRemove) {
            for (Integer candidate : primeNumbers) {
                executors.submit(() -> {
                    try {
                        isPrime(primeNumbers, candidate);
                    } catch (Exception e) {
                        primeNumbersToRemove.add(candidate);
                    }
                });
            }
        }

        // shutdown threads
        executors.shutdownNow();

        // save the result
        for (Integer toRemove : primeNumbersToRemove) {
            primeNumbers.remove(toRemove);
        }

        return primeNumbers;
    }

    /**
     * Determines if given number (candidate) is prime.
     */
    private static void isPrime(List<Integer> primeNumbers, Integer candidate) throws Exception {
        for (Integer j : primeNumbers.subList(0, candidate - 2)) {
            if (candidate % j == 0) {
                throw new Exception();
            }
        }
    }
}