package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> inMemoryHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return List.copyOf(inMemoryHistory);

        // alt.1
        // List<Task> returnedCopy = List.copyOf(inMemoryHistory);
        // return returnedCopy;

        // alt.2
        // return new ArrayList<>(inMemoryHistory);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        inMemoryHistory.add(task);
        if (inMemoryHistory.size() > 10) {
            inMemoryHistory.removeFirst();
        }
    }
}
