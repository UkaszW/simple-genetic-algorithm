import lombok.Data;

import java.nio.ByteBuffer;
import java.util.function.DoubleFunction;

@Data
class Specimen {

    private byte[] genes;
    private double fitness = 0;

    Specimen(byte[] genes) {
        this.genes = genes;
    }

    Specimen(int genomeLength) {
        genes = new byte[genomeLength];

        double value = 2 * Math.random() + 0.5;
        ByteBuffer.wrap(genes).putDouble(value);
    }

    double getFitness(DoubleFunction<Double> function) {
        if (fitness == 0) {
            double value = ByteBuffer.wrap(genes).getDouble();
            if (value >= 0.5 && value <= 2.5) {
                fitness = function.apply(value);
            }
        }
        return fitness;
    }
}
