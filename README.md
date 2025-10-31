# Трекер задач

## Функциональность

### Основные возможности:
- **Управление задачами**: создание, обновление, удаление задач
- **Типы задач**:
    - Task (обычная задача)
    - Epic (сложная задача, состоящая из подзадач)
    - Subtask (подзадача, входящая в Epic)
- **История просмотров**: автоматическое сохранение истории просмотров задач

### Менеджеры задач:
- `InMemoryTaskManager` - хранение задач в оперативной памяти
- `InMemoryHistoryManager` - управление историей просмотров в памяти
- `FileBackedTaskManager` - сохранение задач в файл

## Структура проекта

``` java-kanban/
├── src/
│   ├── manager/                    # Менеджеры задач и сериализация
│   │   ├── InMemoryTaskManager.java
│   │   ├── InMemoryHistoryManager.java
│   │   ├── FileBackedTaskManager.java
│   │   ├── HistoryManager.java
│   │   ├── ManagerSaveException.java
│   │   ├── Managers.java
│   │   ├── TaskManager.java             
│   │   └── TaskTransformer.java   
│   ├── tasks/                      # Модель задач
│   │   ├── Task.java
│   │   ├── Epic.java
│   │   ├── Subtask.java
│   │   ├── TaskStatus.java
│   │   └── TaskType.java
│   └── Main.java                   # Точка входа
│
├── test/
│   └── manager/                    # Юнит-тесты
│       ├── FileBackedTaskManagerTest.java
│       ├── HistoryManagerTest.java
│       └── ManagersTest.java
│       └── TaskManagerTest.java
```

