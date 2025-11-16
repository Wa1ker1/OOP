package ru.nsu.filippova;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс, описывающий структуру данных "Граф".
 * Граф предполагается ОРИЕНТИРОВАННЫМ и ВЗВЕШЕННЫМ.
 *
 * @param <V> Тип данных, хранимых в вершинах.
 */
public interface Graph<V> {

    /**
     * Добавляет вершину в граф.
     *
     * @param vertex вершина для добавления.
     * @return true, если вершина была успешно добавлена, false, если она уже существует.
     */
    boolean addVertex(V vertex);

    /**
     * Удаляет вершину из графа, а также все инцидентные ей (входящие и исходящие) ребра.
     *
     * @param vertex вершина для удаления.
     * @return true, если вершина была найдена и удалена, false в противном случае.
     */
    boolean removeVertex(V vertex);

    /**
     * * Добавляет ориентированное ребро с весом между двумя вершинами.
     *
     * @param source      исходная вершина.
     * @param destination конечная вершина.
     * @param weight      вес ребра.
     * @return true, если ребро было успешно добавлено.
     * @throws IllegalArgumentException если одна из вершин (source или destination)
     * не найдена в графе.
     * @throws IllegalStateException    если ребро между этими вершинами уже существует.
     */
    boolean addEdge(V source, V destination, Integer weight);

    /**
     * Удаляет ребро между двумя вершинами.
     *
     * @param source      исходная вершина.
     * @param destination конечная вершина.
     * @return true, если ребро было найдено и удалено, false в противном случае.
     */
    boolean removeEdge(V source, V destination);

    /**
     * Возвращает вес ребра между двумя вершинами.
     *
     * @param source      исходная вершина.
     * @param destination конечная вершина.
     * @return вес ребра, или null, если ребра не существует.
     */
    Integer getEdgeWeight(V source, V destination);

    /**
     * Проверяет, содержит ли граф указанную вершину.
     *
     * @param vertex вершина для проверки.
     * @return true, если вершина есть в графе, false в противном случае.
     */
    boolean containsVertex(V vertex);

    /**
     * Проверяет, существует ли ребро между двумя вершинами.
     *
     * @param source      исходная вершина.
     * @param destination конечная вершина.
     * @return true, если ребро существует, false в противном случае.
     */
    boolean containsEdge(V source, V destination);

    /**
     * Возвращает множество всех "соседей" (смежных вершин) для данной вершины.
     * "Соседи" - это вершины, в которые ведут ИСХОДЯЩИЕ ребра.
     *
     * @param vertex вершина, для которой ищутся соседи.
     * @return Set соседей. Если вершина не найдена, кидает IllegalArgumentException.
     * @throws IllegalArgumentException если вершина не найдена.
     */
    Set<V> getNeighbors(V vertex);

    /**
     * Возвращает множество всех вершин в графе.
     *
     * @return Set всех вершин.
     */
    Set<V> getVertices();

    /**
     * Возвращает количество вершин в графе.
     *
     * @return количество вершин.
     */
    int getVertexCount();

    /**
     * Возвращает количество ребер в графе.
     *
     * @return количество ребер.
     */
    int getEdgeCount();

    /**
     * Загружает граф из файла в фиксированном формате.
     * Очищает текущий граф перед загрузкой.
     *
     * <p>
     * ВНИМАНИЕ: Для упрощения, данная реализация предполагает,
     * что тип вершины V - это String, а вес ребра представлен типом Integer.
     *
     * <p>
     * Формат:
     * &lt;N - количество вершин&gt;
     * vertexName1
     * vertexName2
     * ...
     * &lt;M - количество ребер&gt;
     * sourceName1 destinationName1 weight1
     * sourceName2 destinationName2 weight2
     * ...
     *
     * @param filePath путь к файлу.
     * @throws IOException           в случае ошибки чтения файла.
     * @throws IllegalStateException если формат файла нарушен.
     */
    void readFromFile(String filePath) throws IOException;

    /**
     * Выполняет топологическую сортировку графа.
     *
     * @return {@link List} вершин в порядке топологической сортировки.
     */
    default List<V> sort() {
        return GraphAlgorithms.topologicalSort(this);
    }
}
