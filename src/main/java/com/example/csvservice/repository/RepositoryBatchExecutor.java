import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Component
public class RepositoryBatchExecutor {

    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * Ejecuta llamadas a un repositorio en lotes.
     *
     * @param items conjunto de elementos a dividir
     * @param batchFunction función que recibe una lista y devuelve resultados
     * @param <T> tipo de entrada (ej: String para número de póliza)
     * @param <R> tipo de salida (ej: Poliza)
     * @return lista completa de resultados acumulados
     */
    public <T, R> List<R> executeInBatches(Set<T> items, Function<List<T>, List<R>> batchFunction) {
        return executeInBatches(items, batchFunction, DEFAULT_BATCH_SIZE);
    }

    public <T, R> List<R> executeInBatches(Set<T> items, Function<List<T>, List<R>> batchFunction, int batchSize) {
        List<R> resultados = new ArrayList<>();
        List<T> lista = new ArrayList<>(items);
        int total = lista.size();

        for (int i = 0; i < total; i += batchSize) {
            int fin = Math.min(i + batchSize, total);
            List<T> sublista = lista.subList(i, fin);
            resultados.addAll(batchFunction.apply(sublista));
        }

        return resultados;
    }
}
