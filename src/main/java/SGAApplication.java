import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SGAApplication {

    public static void main(String... args) {
        List<Double> results = GeneticAlgorithm.GeneticAlgorithm(20, 20, 0.1, 0.1, GeneticAlgorithm.function);

        List<Double> afterCalculations =
                results.stream().map(value -> GeneticAlgorithm.function.apply(value)).collect(Collectors.toList());

        System.out.println("Initial values: ");
        results.forEach(System.out::println);
        System.out.println("=======================");
        System.out.println("Result values: ");
        afterCalculations.forEach(System.out::println);
        System.out.println("=======================");
        System.out.println("Biggest initial value: " + Collections.max(results));
        System.out.println("Biggest result value: " + Collections.max(afterCalculations));
    }
}
