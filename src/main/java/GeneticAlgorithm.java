import org.apache.commons.lang3.mutable.MutableDouble;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleFunction;

public class GeneticAlgorithm {

    public static int GENOME_LENGTH = 8;
    public static DoubleFunction<Double> function = (value -> (Math.exp(value)*Math.sin(10*value*Math.PI) + 1)/value +5);

    /**
     * Runs Simple Genetic Algorithm N times and returns results
     * @param M population size
     * @param N number of attempts
     * @param Pc crossing probability
     * @param Pm mutation probability
     * @param function fitness function
     * @return list of results of running the algorithm N times
     */
    public static List<Double> GeneticAlgorithm(int M, int N, double Pc, double Pm, DoubleFunction<Double> function) {
        List<Double> resultsList = new ArrayList<>();

        for (int i = 0; i< N; i++) {
            resultsList.add(runSGA(M, Pc, Pm, function));
        }
        return resultsList;
    }

    /**
     * Runs Simple Genetic Algorithm
     * @param M population size
     * @param Pc crossing probability
     * @param Pm mutation probability
     * @param function fitness function
     * @return output of the algorithm
     */
    private static double runSGA(int M, double Pc, double Pm, DoubleFunction<Double> function) {
        List<Specimen> population = new ArrayList<>();

        // Creates population
        for (int i = 0; i < M; i++) {
            population.add(new Specimen(GENOME_LENGTH));
        }

        for (int i = 0; i < 100; i++) {
            population = createGeneration(population, Pc, Pm, function);
        }

        return ByteBuffer.wrap(chooseBest(population, function).getGenes()).getDouble();
    }

    /**
     * Creates Generation based on input parameters & function
     * @param population base population
     * @param Pc crossing probability
     * @param Pm mutation probability
     * @param function fitness function
     * @return list of subjects (new generation)
     */
    private static List<Specimen> createGeneration(List<Specimen> population, double Pc, double Pm,
                                                   DoubleFunction<Double> function) {

        population.forEach(specimen -> specimen.getFitness(function));

        List<Specimen> newPopulation = new ArrayList<>();
        population.forEach(specimen -> {

            // Overall population fitness function
            double overallFF = population.stream().mapToDouble(sSpecimen -> sSpecimen.getFitness(function)).sum(); // Consider putting outside of forEach

            double randomizer = Math.random();
            Specimen candidate1 = chooseCandidate(population, overallFF, function);

            // Crossing
            if (randomizer < Pc) newPopulation.add(cross(candidate1, chooseCandidate(population, overallFF, function)));

                // Mutation
            else if (randomizer < Pc + Pm)
                newPopulation.add(mutate(candidate1));

                // Reproduction
            else newPopulation.add(candidate1);
        });

        return newPopulation;
    }

    /**
     * Chooses candidate from population (roulette method)
     * @param population population to choose from
     * @param overallFF overall population fitness funtion
     * @param function function determining principles of choice
     * @return chosen Specimen
     */
    private static Specimen chooseCandidate(List<Specimen> population, double overallFF, DoubleFunction<Double> function) {

        double randomizer = Math.random();
        MutableDouble sum = new MutableDouble(0);

        return population.get((int)(
                population.stream()
                        .mapToDouble(specimen -> specimen.getFitness(function)/overallFF).map(value -> {
                    sum.add(value);
                    return sum.doubleValue();
                }).filter(value -> value < randomizer)
                        .count()));
    }

    /**
     * Chooses best candidate from population based on function (tournament method)
     * @param population population to choose from
     * @param function function determining principles of choice
     * @return best Specimen
     */
    private static Specimen chooseBest(List<Specimen> population, DoubleFunction<Double> function) {
        return population.stream()
                .max(Comparator.comparingDouble(value -> value.getFitness(function))).get();
    }

    /**
     * Mutation method
     * @param candidate candidate to be mutated or not
     * @return new mutated candidate || input candidate
     */
    public static Specimen mutate(Specimen candidate) {
        int randomizer1 = (int) (Math.random()* GENOME_LENGTH);
        int randomizer2 = (int) (Math.random()*8);

        byte[] newGenes = candidate.getGenes().clone();
        newGenes[randomizer1] ^= 1 << randomizer2;

        double success = ByteBuffer.wrap(newGenes).getDouble();
        if (success < 0.5 || success > 2.5) {
            return candidate;
        } else {
            return new Specimen(newGenes);
        }
    }

    /**
     * Crosses two Specimen candidates
     * @param candidate1 first candidate to be crossed
     * @param candidate2 second candidate to be crossed
     * @return new Specimen with crossed genome
     */
    public static Specimen cross(Specimen candidate1, Specimen candidate2) {
        byte[] newGenes = new byte[GENOME_LENGTH];

        for (int i = 0; i < GENOME_LENGTH; i++) {
            for (int j = 0; j < 8; j++) {

                if (Math.random() < 0.5) {
                    if ((candidate1.getGenes()[i] >> j & 1) == 1) newGenes[i] |= 1 << j;
                    else newGenes[i] &= ~(1 << j);
                } else {
                    if ((candidate2.getGenes()[i] >> j & 1) == 1) newGenes[i] |= 1 << j;
                    else newGenes[i] &= ~(1 << j);
                }
            }
        }
        return new Specimen(newGenes);
    }
}

