import lombok.Data;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleFunction;

@Data
class GeneticAlgorithm {

    private static int GENOME_LENGTH = 8;
    static DoubleFunction<Double> FIT_FUNCTION = (value -> (Math.exp(value) * Math.sin(10 * value * Math.PI) + 1) / value + 5);

    static List<Double> runBy(int populationSize, int attempts, double crossingProbability, double mutationProbability) {
        List<Double> results = new ArrayList<>();

        for (int i = 0; i < attempts; i++) {
            results.add(calculateResult(populationSize, crossingProbability, mutationProbability, FIT_FUNCTION));
        }
        return results;
    }

    private static double calculateResult(int populationSize, double crossingProbability, double mutationProbability,
                                          DoubleFunction<Double> fitnessFunction) {
        List<Specimen> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            population.add(new Specimen(GENOME_LENGTH));
        }

        for (int i = 0; i < 100; i++) {
            population = createGeneration(population, crossingProbability, mutationProbability, fitnessFunction);
        }

        return ByteBuffer.wrap(chooseBest(population, fitnessFunction).getGenes()).getDouble();
    }

    private static List<Specimen> createGeneration(List<Specimen> population, double crossingProbability,
                                                   double mutationProbability, DoubleFunction<Double> fitnessFunction) {

        population.forEach(specimen -> specimen.getFitness(fitnessFunction));

        List<Specimen> newPopulation = new ArrayList<>();

        population.forEach(specimen -> {
            double overallFF = population.stream().mapToDouble(s -> s.getFitness(fitnessFunction)).sum();
            double random = Math.random();
            Specimen candidate = chooseCandidate(population, overallFF, fitnessFunction);
            if (random < crossingProbability) {
                newPopulation.add(cross(candidate, chooseCandidate(population, overallFF, fitnessFunction)));
            } else if (random < crossingProbability + mutationProbability) {
                newPopulation.add(mutate(candidate));
            } else {
                newPopulation.add(candidate); // Reproduction
            }
        });

        return newPopulation;
    }

    private static Specimen chooseCandidate(List<Specimen> population, double overallFF,
                                            DoubleFunction<Double> fitnessFunction) {
        double random = Math.random();
        MutableDouble sum = new MutableDouble(0);

        return population.get((int) (population.stream()
                .mapToDouble(specimen -> specimen.getFitness(fitnessFunction) / overallFF).map(value -> {
                    sum.add(value);
                    return sum.doubleValue();
                }).filter(value -> value < random).count()));
    }

    private static Specimen chooseBest(List<Specimen> population, DoubleFunction<Double> function) {
        // tournament method
        return population.stream().max(Comparator.comparingDouble(value -> value.getFitness(function)))
                .orElseThrow(IllegalStateException::new);
    }

    private static Specimen mutate(Specimen candidate) {
        int firstRandom = (int) (Math.random() * GENOME_LENGTH);
        int secondRandom = (int) (Math.random() * GENOME_LENGTH);

        byte[] newGenes = candidate.getGenes().clone();
        newGenes[firstRandom] ^= 1 << secondRandom;

        double success = ByteBuffer.wrap(newGenes).getDouble();
        if (success < 0.5 || success > 2.5) {
            return candidate;
        } else {
            return new Specimen(newGenes);
        }
    }

    private static Specimen cross(Specimen candidate1, Specimen candidate2) {
        byte[] newGenes = new byte[GENOME_LENGTH];

        for (int i = 0; i < GENOME_LENGTH; i++) {
            for (int j = 0; j < GENOME_LENGTH; j++) {
                if (Math.random() < 0.5) {
                    if ((candidate1.getGenes()[i] >> j & 1) == 1) {
                        newGenes[i] |= 1 << j;
                    } else {
                        newGenes[i] &= ~(1 << j);
                    }
                } else {
                    if ((candidate2.getGenes()[i] >> j & 1) == 1) {
                        newGenes[i] |= 1 << j;
                    } else {
                        newGenes[i] &= ~(1 << j);
                    }
                }
            }
        }
        return new Specimen(newGenes);
    }
}

