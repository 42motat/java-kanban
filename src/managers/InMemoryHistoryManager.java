package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> inMemoryHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return inMemoryHistory;
    }

    @Override
    public void add(Task task) {
        if (inMemoryHistory.size() < 10) {
            inMemoryHistory.add(task);
        } else {
            inMemoryHistory.removeFirst();
            inMemoryHistory.add(task);
        }
    }
}
