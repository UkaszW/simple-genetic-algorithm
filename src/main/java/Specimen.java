import lombok.Data;

import java.nio.ByteBuffer;
import java.util.function.DoubleFunction;

@Data
public class Specimen {

    private byte[] genes;
    private double fitness = 0;

    /**
     * Specimen constructor accepting a byte array of genes
     * @param genes gene byte array
     */
    public Specimen(byte[] genes) {
        this.genes = genes;
    }

    /**
     * Specimen constructor accepting a length of genome
     * Creates a Specimen with random genome
     * @param DNALength genome's length
     */
    public Specimen(int DNALength) {
        genes = new byte[DNALength];

        double value = 2*Math.random() + 0.5;
        ByteBuffer.wrap(genes).putDouble(value);
    }

    /**
     * Customized Fitness getter - also calculates fitness value and sets it
     * @param function function accepting double parameter & resulting with double value
     * @return fitness
     */
    public double getFitness(DoubleFunction<Double> function) {

        if (fitness == 0) {
            double value = ByteBuffer.wrap(genes).getDouble();
            if (value >= 0.5 && value <= 2.5) {
                fitness = function.apply(value);
            }
        }
        return fitness;
    }
}
